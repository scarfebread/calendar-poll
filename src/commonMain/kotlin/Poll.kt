import kotlinx.serialization.Serializable

@Serializable
data class Poll(val title: String, var start: String, var end: String, val weekends: Boolean) {
    var id: String? = null
    var createdBy: String? = null
    var current: Boolean = false
    var calendar: List<Calendar.Day>? = null
    var votesStoredInKafka: Boolean? = false

    companion object {
        const val path = "/poll"
        const val cookie = "poll_id"
    }
}