import kotlinx.serialization.Serializable

@Serializable
class Vote(val pollId: String, val date: String, var name: String? = null, var sessionId: String? = null) {
    companion object {
        const val path = "/vote"
    }
}