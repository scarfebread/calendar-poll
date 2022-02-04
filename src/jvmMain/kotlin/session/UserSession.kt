package session

data class UserSession(val id: String) {
    companion object {
        private const val CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"

        fun createSession(): UserSession {
            return UserSession(generateString(30))
        }

        private fun generateString(length: Int): String {
            return (1..length).map { CHAR_SET.random() }.joinToString("")
        }
    }
}