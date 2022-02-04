import kotlinx.serialization.Serializable

@Serializable
data class Poll(val title: String, val start: String, val end: String, val weekends: Boolean) {
    var id: String? = null
    var createdBy: String? = null
    var current: Boolean = false

    companion object {
        const val path = "/poll"
        const val cookie = "poll_id"
    }
}