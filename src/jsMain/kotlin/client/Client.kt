package client

import Poll
import User
import Vote
import config.Config
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*

class Client(config: Config) {
    private val host: String

    init {
        host = config.host
    }

    private val jsonClient = HttpClient {
        install(JsonFeature) { serializer = KotlinxSerializer() }
    }

    suspend fun getPolls(): List<Poll> {
        return jsonClient.get(host + Poll.path)
    }

    suspend fun addPoll(poll: Poll) {
        jsonClient.post<Unit>(host + Poll.path) {
            contentType(ContentType.Application.Json)
            body = poll
        }
    }

    suspend fun deletePoll(poll: Poll) {
        jsonClient.delete<Unit>(host + Poll.path + "/${poll.id}")
    }

    suspend fun createUser(user: User) {
        jsonClient.post<Unit>(host + User.path) {
            contentType(ContentType.Application.Json)
            body = user
        }
    }

    suspend fun getUser(): User? {
        return try {
            jsonClient.get(host + User.path)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun vote(vote: Vote): Poll {
        return jsonClient.post(host + Vote.path) {
            contentType(ContentType.Application.Json)
            body = vote
        }
    }

    suspend fun cancelVote(vote: Vote): Poll {
        return jsonClient.delete(host + Vote.path) {
            contentType(ContentType.Application.Json)
            body = vote
        }
    }
}