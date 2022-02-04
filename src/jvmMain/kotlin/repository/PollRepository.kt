package repository

import Poll

class PollRepository {
    private val polls = mutableListOf(
        Poll("CAOS January meetup", "01/01/2022", "31/01/2022", false),
        Poll("CAOS February meetup", "01/02/2022", "28/02/2022", false)
    )

    fun save(poll: Poll) {
        polls.add(poll)
    }

    fun getPolls(): List<Poll> {
        return polls
    }
}