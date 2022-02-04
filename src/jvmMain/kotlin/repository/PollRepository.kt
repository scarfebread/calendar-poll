package repository

import Poll

class PollRepository {
    private val polls = mutableListOf<Poll>()

    fun save(poll: Poll) {
        polls.add(poll)
    }

    fun findById(id: String): Poll? {
        return polls.firstOrNull { poll ->
            poll.id == id
        }
    }
}