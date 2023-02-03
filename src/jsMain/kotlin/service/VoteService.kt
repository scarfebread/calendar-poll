package service

import Poll
import Vote
import config.Config
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.html.P
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.w3c.dom.EventSource
import react.StateSetter

class VoteService(private val scope: CoroutineScope, private val config: Config) {
    var eventSource: EventSource? = null
    private var listen = false

    private val webSocketClient = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private val jsonClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    fun vote(vote: Vote) {
        sendWs(vote)
    }

    fun cancel(vote: Vote) {
        vote.delete = true
        sendWs(vote)
    }

    fun sendHttp(vote: Vote) {
        scope.launch {
            jsonClient.post(config.host + Vote.path_http) {
                contentType(ContentType.Application.Json)
                setBody(vote)
            }
        }
    }

    fun cancelHttp(vote: Vote) {
        vote.delete = true
        sendHttp(vote)
    }

    private fun sendWs(vote: Vote) {
        scope.launch {
            webSocketClient.webSocket(
                host = config.domain,
                port = config.port,
                path = Vote.path_ws,
                request = {
                    url.protocol = if (config.runningLocally) URLProtocol.WS else URLProtocol.WSS
                }
            ) {
                sendSerialized(vote)
            }
        }
    }

    fun listen(poll: Poll, voteMap: Map<String, Pair<List<Vote>, StateSetter<List<Vote>>>>) {
        scope.launch {
            listen = true

            webSocketClient.webSocket(
                host = config.domain,
                port = config.port,
                path = Vote.path_ws,
                request = {
                    url.protocol = if (config.runningLocally) URLProtocol.WS else URLProtocol.WSS
                }
            ) {
                while (listen) {
                    val vote = receiveDeserialized<Vote>()

                    if (vote.pollId == poll.id) {
                        processVoteEvent(poll, voteMap, vote)
                    }
                }
            }
        }
    }

    fun getVotes(poll: Poll, voteMap: Map<String, Pair<List<Vote>, StateSetter<List<Vote>>>>) {
        val voteService = this

        scope.launch {
            val eventSource = EventSource("${config.host}${Vote.path_http}/${poll.id}")

            voteService.eventSource = eventSource

            eventSource.onmessage = { event ->
                decodeFromString(Vote.serializer(), event.data.toString()).run {
                    processVoteEvent(
                        poll,
                        voteMap,
                        Vote(pollId, this.date, this.name, this.sessionId, this.id, this.delete)
                    )
                }
            }
        }
    }

    private fun processVoteEvent(poll: Poll, voteMap: Map<String, Pair<List<Vote>, StateSetter<List<Vote>>>>, vote: Vote) {
        poll.calendar!!.first { it.date == vote.date }.apply {
            println("received event - ${vote.name} ${vote.date} ${vote.name}")

            val votes = voteMap[vote.date]!!.first.toMutableList()

            println(voteMap[vote.date]!!.first)
            println(votes)

            if (vote.delete) {
                val index = votes.indexOfFirst { it.name == vote.name }
                if (index >= 0) {
                    votes.removeAt(index)
                }
            } else if (votes.firstOrNull { it.date == vote.date && it.name == vote.name } == null) {
                println("received event - adding vote")
                votes.add(vote)
            } else {
                println("not including vote ${vote.date} ${vote.name}")
            }

            println("received event - ${vote.date} ${votes.size}")
            voteMap[vote.date]!!.second(votes)
        }
    }

    fun stop() {
        listen = false
        webSocketClient.close()
    }

    fun close() {
        eventSource?.close()
    }
}