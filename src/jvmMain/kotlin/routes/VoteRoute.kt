package routes

import Vote
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import repository.PollRepository
import repository.UserRepository
import session.UserSession

fun Route.vote(pollRepository: PollRepository, userRepository: UserRepository) {
    post(Vote.path) {
        val session = call.sessions.get<UserSession>()

        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val user = userRepository.findById(session.id)!!
            val vote = call.receive<Vote>()

            vote.sessionId = user.id
            vote.name = user.name

            pollRepository.addVote(vote)

            call.respond(pollRepository.findById(vote.pollId)!!)
        }
    }

    delete(Vote.path) {
        val session = call.sessions.get<UserSession>()

        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val user = userRepository.findById(session.id)!!
            val vote = call.receive<Vote>()

            vote.sessionId = user.id
            vote.name = user.name

            pollRepository.deleteVote(vote)

            call.respond(pollRepository.findById(vote.pollId)!!)
        }
    }
}