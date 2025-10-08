package org.orng.shelezyaka.core.usecases.user

import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.User
import org.orng.shelezyaka.core.domain.models.UserId
import org.orng.shelezyaka.core.domain.repositories.UserRepository
import java.util.UUID

class RegisterUser(
    private val userRepository: UserRepository,
    private val passwordHasher: (String) -> String
) {
    suspend operator fun invoke(username: String, password: String): OperationResult<User> {
        // Валидация
        if (username.isBlank()) {
            return OperationResult.Failure("Username cannot be empty")
        }
        if (password.length < 6) {
            return OperationResult.Failure("Password must be at least 6 characters")
        }

        // Проверка существования пользователя
        val existingUser = userRepository.findByUsername(username)
        if (existingUser != null) {
            return OperationResult.Failure("Username already exists")
        }

        // Создание пользователя
        val user = User(
            id = UserId(UUID.randomUUID()), // ID будет сгенерирован репозиторием
            username = username,
            passwordHash = passwordHasher(password),
            createdAt = System.currentTimeMillis()
        )

        return userRepository.save(user)
    }
}