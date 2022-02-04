package routes

import Poll
import User
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
        val session = call.sessions.get<UserSession>()!!
        val user = userRepository.findById(session.id)
        val currentPoll = call.request.cookies.rawCookies[Poll.cookie]
        val polls = mutableListOf<Poll>()

        user?.polls?.forEach { pollId ->
            val poll = pollRepository.findById(pollId)!!
            if (poll.id == pollId) {
                poll.current = currentPoll == pollId
            }
            polls.add(poll)
        }

        call.response.cookies.append(Cookie(Poll.cookie, ""))
        call.respond(polls)
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

    get(Poll.path + "/{pollId}") {
        var session = call.sessions.get<UserSession>()
        if (session == null) {
            session = UserSession.createSession()
            call.sessions.set(session)
        }

        val pollId = call.parameters["pollId"]!!
        var user = userRepository.findById(session.id)
        val poll = pollRepository.findById(pollId)

        if (poll != null) {
            if (user == null) {
                user = User(session.id, null)
            }

            if (!user.polls.contains(pollId)) {
                user.addPoll(pollId)
                userRepository.save(user)
            }

            call.response.cookies.append(Cookie("poll_id", pollId))
        }

        call.respondRedirect("/")
    }
}