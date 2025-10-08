package org.orng.shelezyaka.database.repositories

import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.User
import org.orng.shelezyaka.core.domain.models.UserId
import org.orng.shelezyaka.core.domain.repositories.UserRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryUserRepository : UserRepository {
    private val users = ConcurrentHashMap<UserId, User>()

    override suspend fun findById(id: UserId): User? = users[id]

    override suspend fun findByUsername(username: String): User? =
        users.values.find { it.username == username }

    override suspend fun save(user: User): OperationResult<User> {
        users[user.id] = user
        return OperationResult.Success(user)
    }

    override suspend fun authenticate(username: String, password: String): OperationResult<User> {
        val user = findByUsername(username) ?:
        return OperationResult.Failure("User not found")

        // В реальности здесь будет хеширование пароля
        return if (user.passwordHash == password) {
            OperationResult.Success(user)
        } else {
            OperationResult.Failure("Invalid password")
        }
    }

    override suspend fun updateLastLogin(userId: UserId, timestamp: Long): OperationResult<Unit> {
        val user = users[userId] ?: return OperationResult.Failure("User not found")
        users[userId] = user.copy(lastLogin = timestamp)
        return OperationResult.Success(Unit)
    }
}