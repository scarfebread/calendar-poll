package repository

import User

class UserRepository {
    private val users = mutableListOf<User>()

    fun save(user: User) {
        if (user.id != null) {
            findById(user.id!!)?.apply {
                this.name = user.name
                this.polls = user.polls
                users.add(this)
                return
            }
        }

        users.add(user)
    }

    fun findById(id: String): User? {
        return users.firstOrNull { user ->
            user.id == id
        }
    }
}