package service

import client.Client
import Vote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class VoteService(private val scope: CoroutineScope, private val client: Client) {
    fun vote(vote: Vote) {
        scope.launch {
            client.vote(vote)
        }
    }

    fun cancel(vote: Vote) {
        scope.launch {
            client.cancelVote(vote)
        }
    }
}