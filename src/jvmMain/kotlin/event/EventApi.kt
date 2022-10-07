package event

import Vote
import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class EventApi(private val sqs: SqsClient) {
    fun create(vote: Vote) {
        sqs.sendMessage(
            SendMessageRequest
                .builder()
                .queueUrl("https://sqs.eu-west-1.amazonaws.com/759614112154/calendar-events")
                .messageBody(Json.encodeToString(Vote.serializer(), vote))
                .delaySeconds(5)
                .build()
        )
    }
}