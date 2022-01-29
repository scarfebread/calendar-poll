import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*

const val endpoint = "http://0.0.0.0:9090"

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getPolls(): List<Poll> {
    return jsonClient.get(endpoint + Poll.path)
}

suspend fun addPoll(poll: Poll) {
    jsonClient.post<Unit>(endpoint + Poll.path) {
        contentType(ContentType.Application.Json)
        body = poll
    }
}

suspend fun deletePoll(poll: Poll) {
    jsonClient.delete<Unit>(endpoint + Poll.path + "/${poll.id}")
}