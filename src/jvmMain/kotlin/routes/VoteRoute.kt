package routes

import Vote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import repository.PollRepository
import repository.UserRepository
import session.UserSession
import session.UserSession.Companion.generateString

fun Route.vote(pollRepository: PollRepository, userRepository: UserRepository) {
    authenticate {
        post(Vote.path) {
            val session = call.sessions.get<UserSession>()!!

            val user = userRepository.findById(session.id)!!
            val vote = call.receive<Vote>()

            vote.id = generateString(20)
            vote.sessionId = user.id
            vote.name = user.name

            pollRepository.addVote(vote)

            call.respond(pollRepository.findById(vote.pollId)!!)
        }
    }

    authenticate {
        delete(Vote.path) {
            val session = call.sessions.get<UserSession>()!!

            val user = userRepository.findById(session.id)!!
            val vote = call.receive<Vote>()

            vote.sessionId = user.id
            vote.name = user.name

            pollRepository.deleteVote(vote)

            call.respond(pollRepository.findById(vote.pollId)!!)
        }
    }
}