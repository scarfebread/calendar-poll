package session

import io.ktor.server.auth.*

data class UserSession(val id: String): Principal {
    companion object {
        const val COOKIE_NAME = "user_session"
        private const val CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"

        fun createSession(): UserSession {
            return UserSession(generateString(30))
        }

        fun generateString(length: Int): String {
            return (1..length).map { CHAR_SET.random() }.joinToString("")
        }
    }
}