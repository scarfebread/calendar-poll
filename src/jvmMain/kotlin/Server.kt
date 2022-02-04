import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import repository.PollRepository
import repository.UserRepository
import routes.index
import routes.poll
import routes.user
import session.UserSession

fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        install(Sessions) {
            cookie<UserSession>(UserSession.COOKIE_NAME)
        }

        val userRepository = UserRepository()
        val pollRepository = PollRepository()

        routing {
            index()
            poll(pollRepository, userRepository)
            user(userRepository)
        }
    }.start(wait = true)
}