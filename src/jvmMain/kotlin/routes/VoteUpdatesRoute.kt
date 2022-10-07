package routes

import Vote
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import repository.PollRepository
import repository.UserRepository
import session.UserSession
import java.util.Collections
import java.util.concurrent.CancellationException

fun Route.voteUpdates(pollRepository: PollRepository, userRepository: UserRepository) {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    authenticate {
        webSocket(Vote.path) {
            val thisConnection = Connection(this)
            connections += thisConnection

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue

                    val vote = Json.decodeFromString(
                        Vote.serializer(),
                        frame.readText()
                    )
                    val user = userRepository.findById(
                        call.sessions.get<UserSession>()!!.id
                    )!!
                    vote.sessionId = user.id
                    vote.name = user.name

                    if (vote.delete) {
                        pollRepository.deleteVote(vote)
                    } else {
                        vote.id = UserSession.generateString(20)

                        pollRepository.addVote(vote)
                    }

                    // TODO these should be centralised via the DB
                    connections.forEach {
                        it.session.send(
                            // TODO better way to do this?
                            Json.encodeToString(Vote.serializer(), vote)
                        )
                    }
                }
            } catch (cancellationException: CancellationException) {
                // exceptions will be thrown when channels are closed, however this is expected
            } catch (e: Exception) {
                println(e)
            } finally {
                connections -= thisConnection
            }
        }
    }
}

class Connection(val session: DefaultWebSocketSession)