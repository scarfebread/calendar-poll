package repository

import Poll
import Vote
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import service.generateCalendar
import session.UserSession.Companion.generateString
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

class PollRepository(private val collection: CoroutineCollection<Poll>, private val dynamo: DynamoDbClient) {
    suspend fun save(poll: Poll, updateMongo: Boolean = true) {
        if (updateMongo) {
            collection.insertOne(poll)
        }

        dynamo.putItem(
            PutItemRequest.builder()
                .tableName(TABLE)
                .item(hashMapOf(
                    "pk" to pollId(poll.id!!),
                    "sk" to string("poll"),
                    "name" to string(poll.title),
                    "start" to string(poll.start),
                    "end" to string(poll.end),
                    "weekends" to boolean(poll.weekends),
                    "createdBy" to string(poll.createdBy),
                ))
                .build()
        )
    }

    suspend fun findById(id: String): Poll? {
        val poll: Poll?

        val getResponse = dynamo.getItem(
            GetItemRequest.builder()
                .key(hashMapOf(
                    "pk" to pollId(id),
                    "sk" to string("poll")
                ))
                .tableName(TABLE)
                .build()
        ).item()

        if (getResponse.isEmpty()) {
            poll = collection.findOne(Poll::id eq id)

            if (poll != null) {
                this.save(poll, false)
                poll.calendar!!.forEach { day ->
                    day.votes.forEach { vote ->
                        if (vote.id == null) {
                            vote.id = generateString(20)
                        }
                        this.addVote(vote)
                    }
                }
            }

            return poll
        }

        poll = Poll(
            toString(getResponse["name"])!!,
            toString(getResponse["start"])!!,
            toString(getResponse["end"])!!,
            toBoolean(getResponse["weekends"]),
        ).apply {
            this.id = toString(getResponse["pk"])!!
            this.createdBy = toString(getResponse["createdBy"])!!
        }

        // TODO break out the query and get
        val queryResponse = dynamo.query(
            QueryRequest.builder()
                .tableName(TABLE)
                .keyConditionExpression("pk = :pollId and begins_with(sk, :votePrefix)")
                .expressionAttributeValues(hashMapOf(
                    ":pollId" to pollId(poll.id!!),
                    ":votePrefix" to string("vote-")
                ))
                .build()
        )

        val votes = queryResponse.items().map {
            Vote(
                toString(it["pk"])!!,
                toString(it["date"])!!,
                toString(it["name"]),
                toString(it["sessionId"]),
                toString(it["sk"]),
            )
        }

        poll.calendar = generateCalendar(poll.start, poll.end, poll.weekends, votes)

        return poll
    }

    suspend fun addVote(vote: Vote, updateMongo: Boolean = true) {
        if (updateMongo) {
            val query = Document(
                "\$and", listOf(
                    Poll::id eq vote.pollId,
                    Document("calendar.date", vote.date)
                )
            )

            val update = Document(
                "\$addToSet",
                Document(
                    "calendar.$.votes",
                    vote
                )
            )

            collection.updateOne(
                query,
                update,
            )
        }

        dynamo.putItem(
            PutItemRequest.builder()
                .tableName(TABLE)
                .item(hashMapOf(
                    "pk" to pollId(vote.pollId),
                    "sk" to voteId(vote.id!!),
                    "date" to string(vote.date),
                    "name" to string(vote.name),
                    "sessionId" to string(vote.sessionId)
                ))
                .build()
        )
    }

    suspend fun deleteVote(vote: Vote) {
        val query = Document(
            "\$and", listOf(
                Poll::id eq vote.pollId,
                Document("calendar.date", vote.date)
            )
        )

        val update = Document(
            "\$pull",
            Document(
                "calendar.$.votes",
                Document("sessionId", vote.sessionId)
            )
        )

        collection.updateOne(
            query,
            update,
        )

        dynamo.query(
            QueryRequest.builder()
                .tableName(TABLE)
                .indexName(VOTE_INDEX)
                .keyConditionExpression("sessionId = :sessionId and #voteDate = :date")
                .expressionAttributeNames(hashMapOf(
                    "#voteDate" to "date"
                ))
                .expressionAttributeValues(hashMapOf(
                    ":sessionId" to string(vote.sessionId),
                    ":date" to string(vote.date)
                ))
                .build()
        ).items().forEach {
            dynamo.deleteItem(
                DeleteItemRequest.builder()
                    .tableName(TABLE)
                    .key(
                        hashMapOf(
                            "pk" to pollId(toString(it["pk"])!!),
                            "sk" to voteId(toString(it["sk"])!!)
                        )
                    )
                    .build()
            )
        }
    }
}