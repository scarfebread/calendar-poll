package repository

import Poll
import Vote
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class PollRepository(private val collection: CoroutineCollection<Poll>) {
    suspend fun save(poll: Poll) {
        collection.insertOne(poll)
    }

    suspend fun findById(id: String): Poll? {
        return collection.findOne(Poll::id eq id)
    }

    suspend fun addVote(vote: Vote) {
        // TODO nicer way to do this?
        val query = Document(
            "\$and", listOf(
                Poll::id eq vote.pollId,
                Document("calendar.date", vote.date)
            )
        )

        val update = Document(
            "\$addToSet",
            Document(
                "calendar.$.votes",
                vote
            )
        )

        collection.updateOne(
            query,
            update,
        )
    }
}