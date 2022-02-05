import kotlinx.serialization.Serializable

class Calendar {
    @Serializable
    data class Day(val date: String, val week: Int, val day: Int) {
        val votes = mutableListOf<String>()
    }
}