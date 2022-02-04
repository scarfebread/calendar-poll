package routes

import Poll
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import repository.PollRepository
import repository.UserRepository
import session.UserSession
import session.UserSession.Companion.generateString

fun Route.poll(pollRepository: PollRepository, userRepository: UserRepository) {
    get(Poll.path) {
        call.respond(pollRepository.getPolls())
    }

    post(Poll.path) {
        val session = call.sessions.get<UserSession>()

        // TODO add authentication instead of checking session
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val poll = call.receive<Poll>()
            val user = userRepository.findById(session.id)

            poll.createdBy = session.id
            poll.id = generateString(20)
            pollRepository.save(poll)

            if (user != null) {
                user.polls.add(poll.id!!)
                userRepository.save(user)
            }

            call.respond(HttpStatusCode.Created)
        }
    }
}