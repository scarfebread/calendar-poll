import kotlinx.serialization.Serializable

@Serializable
data class Poll(val title: String, val start: String, val end: String, val weekends: Boolean) {
    val id: Int = title.hashCode()

    companion object {
        const val path = "/calendar-poll"
    }
}