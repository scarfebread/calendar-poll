package session

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*

fun Authentication.Configuration.configureSessionAuthentication() {
    session<UserSession> {
        validate { session ->
            session
        }
        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}