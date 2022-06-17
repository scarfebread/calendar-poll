package service

import Poll
import Vote
import config.Config
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import react.StateSetter

class VoteService(private val scope: CoroutineScope, private val config: Config) {
    private var listen = false

    private val webSocketClient = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    fun vote(vote: Vote) {
        send(vote)
    }

    fun cancel(vote: Vote) {
        vote.delete = true
        send(vote)
    }

    private fun send(vote: Vote) {
        scope.launch {
            webSocketClient.webSocket(
                host = config.domain,
                port = config.port,
                path = Vote.path,
                request = {
                    url.protocol = if (config.runningLocally) URLProtocol.WS else URLProtocol.WSS
                }
            ) {
                sendSerialized(vote)
            }
        }
    }

    fun listen(poll: Poll, voteMap: Map<String, StateSetter<Int>>) {
        scope.launch {
            listen = true

            webSocketClient.webSocket(
                host = config.domain,
                port = config.port,
                path = Vote.path,
                request = {
                    url.protocol = if (config.runningLocally) URLProtocol.WS else URLProtocol.WSS
                }
            ) {
                while (listen) {
                    val vote = receiveDeserialized<Vote>()

                    if (vote.pollId == poll.id) {
                        poll.calendar!!.first { it.date == vote.date }.apply {
                            if (vote.delete) {
                                val index = votes.indexOfFirst { it.name == vote.name }
                                if (index >= 0) {
                                    votes.removeAt(index)
                                }
                            } else if (!votes.contains(vote)) {
                                votes.add(vote)
                            }

                            voteMap[vote.date]!!.invoke(votes.size)
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        listen = false
        webSocketClient.close()
    }
}