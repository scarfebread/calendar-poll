package client

import Poll
import User
import Vote
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*

class Client {
    private val jsonClient = HttpClient {
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

    suspend fun createUser(user: User) {
        jsonClient.post<Unit>(endpoint + User.path) {
            contentType(ContentType.Application.Json)
            body = user
        }
    }

    suspend fun getUser(): User? {
        return try {
            jsonClient.get(endpoint + User.path)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun vote(vote: Vote): Poll {
        return jsonClient.post(endpoint + Vote.path) {
            contentType(ContentType.Application.Json)
            body = vote
        }
    }

    suspend fun cancelVote(vote: Vote): Poll {
        return jsonClient.delete(endpoint + Vote.path) {
            contentType(ContentType.Application.Json)
            body = vote
        }
    }

    companion object {
        const val endpoint = "http://0.0.0.0:9090"
    }
}