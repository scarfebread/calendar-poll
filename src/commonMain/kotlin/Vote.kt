import kotlinx.serialization.Serializable

@Serializable
data class Vote(val pollId: String, val date: String, var name: String? = null, var sessionId: String? = null, var id: String? = null, var delete: Boolean = false) {
    companion object {
        const val path = "/vote"
    }
}