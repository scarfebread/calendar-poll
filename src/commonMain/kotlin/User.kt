import kotlinx.serialization.Serializable

@Serializable
data class User(var id: String?, var name: String?, var polls: MutableList<String> = mutableListOf()) {
    fun addPoll(poll: String) {
        polls.add(poll)
    }

    fun deletePoll(poll: String) {
        polls.remove(poll)
    }

    companion object {
        const val path = "/user"
    }
}