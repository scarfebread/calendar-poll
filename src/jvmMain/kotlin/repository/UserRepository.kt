package repository

import User
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class UserRepository(private val collection: CoroutineCollection<User>) {
    suspend fun save(user: User) {
        collection.updateOne(
            User::id eq user.id,
            user,
            UpdateOptions().upsert(true)
        )
    }

    suspend fun findById(id: String): User? {
        return collection.findOne(User::id eq id)
    }
}