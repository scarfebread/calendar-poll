package routes

import Vote
import event.KafkaEventService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import repository.UserRepository
import session.UserSession

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
}