package repository

import User
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest


class UserRepository(private val dynamo: DynamoDbClient) {
    fun save(user: User) {
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

    fun addPollToUser(user: User, pollId: String) {
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

    fun findById(id: String): User? {
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
            return null
        }

        val user = User(toString(getResponse["pk"]), toString(getResponse["name"]))

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