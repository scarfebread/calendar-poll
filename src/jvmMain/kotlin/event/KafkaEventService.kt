package event

import Vote
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

class KafkaEventService(private val producer: KafkaProducer<String, Vote>, private val adminClient: AdminClient) {
    fun createVoteEvent(vote: Vote) {
        val topic = "calendar_votes_${vote.pollId}"
        producer.send(ProducerRecord(topic, vote.id, vote)) { _: RecordMetadata, e: Exception? ->
            e?.printStackTrace()
        }
    }

    fun deleteVote(vote: Vote) {
        val topic = "calendar_votes_${vote.pollId}"
        producer.send(ProducerRecord(topic, vote.id, vote)) { _: RecordMetadata, e: Exception? ->
            e?.printStackTrace()
        }
    }

    fun createTopic(pollId: String) {
        val topic = "calendar_votes_${pollId}"
        val newTopic = NewTopic(topic, PARTITIONS, REPLICATION_FACTOR)

        adminClient.createTopics(listOf(newTopic)).all()
    }

    companion object {
        private const val PARTITIONS = 1
        private const val REPLICATION_FACTOR: Short = 1
    }
}
