package repository

import Poll
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class PollRepository(private val collection: CoroutineCollection<Poll>) {
    suspend fun save(poll: Poll) {
        collection.insertOne(poll)
    }

    suspend fun findById(id: String): Poll? {
        return collection.findOne(Poll::id eq id)
    }
}