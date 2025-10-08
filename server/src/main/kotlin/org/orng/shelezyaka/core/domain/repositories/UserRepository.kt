package org.orng.shelezyaka.core.domain.repositories

import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.User
import org.orng.shelezyaka.core.domain.models.UserId

interface UserRepository {
    suspend fun findById(id: UserId): User?
    suspend fun findByUsername(username: String): User?
    suspend fun save(user: User): OperationResult<User>
    suspend fun authenticate(username: String, password: String): OperationResult<User>
    suspend fun updateLastLogin(userId: UserId, timestamp: Long): OperationResult<Unit>
}