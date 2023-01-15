package routes

import Poll
import User
import event.KafkaEventService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import repository.PollRepository
import repository.UserRepository
import service.generateCalendar
import service.validateEnd
import service.validateStart
import session.UserSession
import session.UserSession.Companion.generateString

fun Route.poll(pollRepository: PollRepository, userRepository: UserRepository, kafkaEventService: KafkaEventService) {
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

        call.respond(polls)
    }

    authenticate {
        post(Poll.path) {
            val session = call.sessions.get<UserSession>()!!

            val poll = call.receive<Poll>()
            val user = userRepository.findById(session.id)

            poll.createdBy = session.id
            poll.id = generateString(20)
            poll.start = validateStart(poll.start, poll.weekends)
            poll.end = validateEnd(poll.end, poll.weekends)
            poll.calendar = generateCalendar(poll.start, poll.end, poll.weekends)
            poll.votesStoredInKafka = true

            pollRepository.save(poll)
            kafkaEventService.createTopic(poll.id!!)

            if (user != null) {
                user.polls.add(poll.id!!)
                userRepository.addPollToUser(user, poll.id!!)
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
                userRepository.save(user)
            }

            if (!user.polls.contains(pollId)) {
                user.addPoll(pollId)
                userRepository.addPollToUser(user, pollId)
            }

            call.response.cookies.append(Cookie(Poll.cookie, pollId))
        }

        call.respondRedirect("/")
    }
}