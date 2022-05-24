package client

import Poll
import User
import Vote
import config.Config
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class Client(config: Config) {
    private val host: String

    init {
        host = config.host
    }

    private val jsonClient = HttpClient {
        install(ContentNegotiation) { json() }
        install(WebSockets)
    }

    suspend fun getPolls(): List<Poll> {
        return jsonClient.get(host + Poll.path).body()
    }

    suspend fun addPoll(poll: Poll) {
        jsonClient.post(host + Poll.path) {
            contentType(ContentType.Application.Json)
            setBody(poll)
        }
    }

    suspend fun deletePoll(poll: Poll) {
        jsonClient.delete(host + Poll.path + "/${poll.id}")
    }

    suspend fun createUser(user: User): User {
        return jsonClient.post(host + User.path) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    suspend fun getUser(): User? {
        return try {
            jsonClient.get(host + User.path).body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun vote(vote: Vote): Poll {
        return jsonClient.post(host + Vote.path) {
            contentType(ContentType.Application.Json)
            setBody(vote)
        }.body()
    }

    suspend fun cancelVote(vote: Vote): Poll {
        return jsonClient.delete(host + Vote.path) {
            contentType(ContentType.Application.Json)
            setBody(vote)
        }.body()
    }
}