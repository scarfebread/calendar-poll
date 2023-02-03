package routes

import Vote
import event.KafkaEventService
import event.kafkaConsumerConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
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
            vote.id = UserSession.generateString(20)

            kafkaEventService.createVoteEvent(vote)

            call.respond(HttpStatusCode.Created)
        }

        get(Vote.path_http + "/{pollId}") {
            val pollId = call.parameters["pollId"]!!

            val clientId = "vote-consumer-" + pollId + "-" + UUID.randomUUID()

            val config = kafkaConsumerConfig
            config.apply {
                this[ConsumerConfig.CLIENT_ID_CONFIG] = clientId
                this[ConsumerConfig.GROUP_ID_CONFIG] = clientId
                this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
                this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            }

            val receiverOptions = ReceiverOptions.create<Int, String>(config)
            val options = receiverOptions.subscription(Collections.singleton("calendar_votes_${pollId}"))

            val kafkaFlux = KafkaReceiver.create(options).receive()

            val events = mutableListOf<String>()
            val sentEvents = mutableListOf<String>()

            kafkaFlux.subscribe {
                events.add(it.value())
            }

            val channel = produce {
                while (true) {
                    for (event in events) {
                        if (!sentEvents.contains(event)) {
                            send(event)
                            sentEvents.add(event)
                        }
                    }

                    withContext(Dispatchers.IO) {
                        Thread.sleep(100)
                    }
                }
            }.broadcast()

            call.respondSse(
                channel.openSubscription()
            )
        }
    }
}

suspend fun ApplicationCall.respondSse(events: ReceiveChannel<String>) {
    response.cacheControl(CacheControl.NoCache(null))

    respondTextWriter(contentType = ContentType.Text.EventStream) {
        for (event in events) {
            for (dataLine in event.lines()) {
                write("data: $dataLine\n")
            }
            write("\n")
            flush()
        }
    }
}
