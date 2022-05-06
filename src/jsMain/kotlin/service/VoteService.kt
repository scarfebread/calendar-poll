package service

import Poll
import client.Client
import Vote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class VoteService(private val scope: CoroutineScope, private val client: Client, private var updatePoll: (Poll?) -> Unit) {
    fun vote(vote: Vote) {
        scope.launch {
            updatePoll(
                client.vote(vote)
            )
        }
    }

    fun cancel(vote: Vote) {
        scope.launch {
            updatePoll(
                client.cancelVote(vote)
            )
        }
    }
}