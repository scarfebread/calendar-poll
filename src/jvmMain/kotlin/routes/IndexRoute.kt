package routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import session.UserSession

fun Route.index() {
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