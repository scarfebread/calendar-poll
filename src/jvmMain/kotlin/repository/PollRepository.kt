package repository

import Poll

class PollRepository {
    private val polls = mutableListOf<Poll>()

    fun save(poll: Poll) {
        polls.add(poll)
    }

    fun getPolls(): List<Poll> {
        return polls
    }
}