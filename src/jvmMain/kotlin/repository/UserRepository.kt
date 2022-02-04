package repository

import User

class UserRepository {
    private val users = mutableListOf<User>()

    fun save(user: User) {
        users.add(user)
    }

    fun findById(id: String): User? {
        return users.firstOrNull { user ->
            user.id == id
        }
    }
}