import kotlinx.serialization.Serializable

@Serializable
data class User(val desc: String, val priority: Int) {
    val id: Int = desc.hashCode() // TODO UUID

    companion object {
        const val path = "/calendar-poll"
    }
}