package routes

import Vote
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import repository.PollRepository
import repository.UserRepository
import session.UserSession
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

fun Route.voteUpdates(pollRepository: PollRepository, userRepository: UserRepository) {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    authenticate {
        webSocket("/poll-updates") {
            val connection = Connection(this)
            connections += connection

            try {
                val vote = receiveDeserialized<Vote>()
                val session = call.sessions.get<UserSession>()!!
                val user = userRepository.findById(session.id)!!
                vote.sessionId = user.id
                vote.name = user.name

                if (vote.delete) {
                    pollRepository.deleteVote(vote)
                } else {
                    vote.id = UserSession.generateString(20)

                    pollRepository.addVote(vote)
                }

                connections.forEach {
                    it.session.send(Json.encodeToString(Vote.serializer(), vote))
                }
            } catch (e: Exception) {
                println("exception thown")
                e.printStackTrace()
            }
        }
    }
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val name = "user${lastId.getAndIncrement()}"
}