package repository

import Poll
import Vote
import event.EventApi
import service.generateCalendar
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

class PollRepository(private val dynamo: DynamoDbClient, private val eventApi: EventApi) {
    fun save(poll: Poll) {
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
                    "votesStoredInKafka" to boolean(poll.votesStoredInKafka!!)
                ))
                .build()
        )
    }

    fun findById(id: String): Poll? {
        val getResponse = dynamo.getItem(
            GetItemRequest.builder()
                .key(hashMapOf(
                    "pk" to pollId(id),
                    "sk" to string("poll")
                ))
                .tableName(TABLE)
                .build()
        ).item()

        val poll = Poll(
            toString(getResponse["name"])!!,
            toString(getResponse["start"])!!,
            toString(getResponse["end"])!!,
            toBoolean(getResponse["weekends"]),
        ).apply {
            this.id = toString(getResponse["pk"])!!
            this.createdBy = toString(getResponse["createdBy"])!!
            this.votesStoredInKafka = toBoolean(getResponse["votesStoredInKafka"])
        }

        var votes = listOf<Vote>()

        if (poll.votesStoredInKafka == false) {
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

            votes = queryResponse.items().map {
                Vote(
                    toString(it["pk"])!!,
                    toString(it["date"])!!,
                    toString(it["name"]),
                    toString(it["sessionId"]),
                    toString(it["sk"]),
                )
            }
        }

        poll.calendar = generateCalendar(poll.start, poll.end, poll.weekends, votes)

        return poll
    }

    fun addVote(vote: Vote) {
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

    fun deleteVote(vote: Vote) {
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