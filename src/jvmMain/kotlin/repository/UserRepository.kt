package repository

import User
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest


class UserRepository(private val collection: CoroutineCollection<User>, private val dynamo: DynamoDbClient) {
    suspend fun save(user: User) {
        collection.updateOne(
            User::id eq user.id,
            user,
            UpdateOptions().upsert(true)
        )

        dynamo.putItem(
            PutItemRequest.builder()
                .tableName(TABLE)
                .item(hashMapOf(
                    "pk" to userId(user.id!!),
                    "sk" to string("user"),
                    "name" to string(user.name)
                ))
                .build()
        )
    }

    suspend fun addPollToUser(user: User, pollId: String) {
        collection.updateOne(
            User::id eq user.id,
            user,
            UpdateOptions().upsert(true)
        )

        dynamo.putItem(
            PutItemRequest.builder()
                .tableName(TABLE)
                .item(hashMapOf(
                    "pk" to userId(user.id!!),
                    "sk" to pollId(pollId)
                ))
                .build()
        )
    }

    suspend fun findById(id: String): User? {
        var user: User?

        val getResponse = dynamo.getItem(
            GetItemRequest.builder()
                .key(hashMapOf(
                    "pk" to userId(id),
                    "sk" to string("user")
                ))
                .tableName(TABLE)
                .build()
        ).item()

        if (getResponse.isEmpty()) {
            // TODO update dynamo if not null
            return collection.findOne(User::id eq id)
        }

        user = User(toString(getResponse["pk"]), toString(getResponse["name"]))

        // TODO break out the query and get
        val queryResponse = dynamo.query(
            QueryRequest.builder()
                .tableName(TABLE)
                .keyConditionExpression("pk = :userId and begins_with(sk, :pollPrefix)")
                .expressionAttributeValues(hashMapOf(
                    ":userId" to userId(user.id!!),
                    ":pollPrefix" to string("poll-")
                ))
                .build()
        )

        queryResponse.items().forEach {
            user.addPoll(toString(it["sk"])!!)
        }

        return user
    }
}