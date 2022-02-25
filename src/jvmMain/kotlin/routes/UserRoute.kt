package routes

import User
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import repository.UserRepository
import session.UserSession

fun Route.user(userRepository: UserRepository) {
    get(User.path) {
        val session = call.sessions.get<UserSession>()

        if (session != null) {
            val user = userRepository.findById(session.id)
            if (user != null) {
                call.respond(user)
            }
        }
    }
    post(User.path) {
        val session = call.sessions.get<UserSession>()
        val user = call.receive<User>()
        user.id = session!!.id

        val storedUser = userRepository.findById(user.id!!)

        if (storedUser == null) {
            userRepository.save(user)
        } else {
            storedUser.name = user.name
            userRepository.save(storedUser)
        }

        call.respond(HttpStatusCode.Accepted, "Accepted")
    }
}