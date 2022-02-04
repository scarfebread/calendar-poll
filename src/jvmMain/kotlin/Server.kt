import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import repository.PollRepository
import repository.UserRepository
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
            cookie<UserSession>("user_session")
        }

        val userRepository = UserRepository()
        val pollRepository = PollRepository()

        routing {
            get(Poll.path) {
                call.respond(pollRepository.getPolls())
            }
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

                userRepository.save(user)

                call.respond(HttpStatusCode.Accepted, "Accepted")
            }
            get("/") {
                call.sessions.get<UserSession>() ?: call.sessions.set(UserSession.createSession())

                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)
}