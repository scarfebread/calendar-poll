import kotlinx.serialization.Serializable

@Serializable
data class User(var id: String?, val name: String) {
    companion object {
        const val path = "/user"
    }
}