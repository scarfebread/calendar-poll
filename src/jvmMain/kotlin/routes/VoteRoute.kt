package routes

import Vote
import event.KafkaEventService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord
import repository.UserRepository
import session.UserSession
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun Route.vote(userRepository: UserRepository, kafkaEventService: KafkaEventService) {
    authenticate {
        post(Vote.path_http) {
            val session = call.sessions.get<UserSession>()!!
            val vote = call.receive<Vote>()

            val user = userRepository.findById(session.id)!!
            vote.sessionId = user.id
            vote.name = user.name

            kafkaEventService.createVoteEvent(vote)

            call.respond(HttpStatusCode.Created)
        }
    }

    get(Vote.path_http + "/{pollId}") {
        val pollId = call.parameters["pollId"]!!
        val clientId = "vote-consumer-" + pollId + "-" + UUID.randomUUID()
        val kafkaConfig = HashMap<String, Any>().apply {
            this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "pkc-e8mp5.eu-west-1.aws.confluent.cloud:9092"
            this[ConsumerConfig.CLIENT_ID_CONFIG] = clientId
            this[ConsumerConfig.GROUP_ID_CONFIG] = clientId
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

            this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"

            this[SaslConfigs.SASL_JAAS_CONFIG] = "org.apache.kafka.common.security.plain.PlainLoginModule required username='TNJVLXNAIZZ5RWDC' password='rdpAlMTV1BrXFK5fxBimUpIY2hDz7OUdCkINrqvKTYYyU4vtSttyvvvUgS5RRxY/';"
            this[SaslConfigs.SASL_MECHANISM] = "PLAIN"
        }

        val receiverOptions = ReceiverOptions.create<Int, Vote>(kafkaConfig)
        val options: ReceiverOptions<Int, Vote> = receiverOptions.subscription(Collections.singleton("calendar_votes_${pollId}"))

        val kafkaFlux: Flux<ReceiverRecord<Int, Vote>> = KafkaReceiver.create(options).receive()

        val events = mutableListOf<Vote>()
        kafkaFlux.subscribe {
            events.add(it.value())
        }

        val channel = produce {
            for (event in events) {
                send(event)
            }
        }

        call.respondSse(
            channel
        )
    }
}

suspend fun ApplicationCall.respondSse(events: ReceiveChannel<Vote>) {
    response.cacheControl(CacheControl.NoCache(null))

    respondTextWriter(contentType = ContentType.Text.EventStream) {
        for (event in events) {
            write("${event.sessionId}\n")
            write("\n")
            flush()
        }
    }
}