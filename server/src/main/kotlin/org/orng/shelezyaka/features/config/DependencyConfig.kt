package org.orng.shelezyaka.features.config

import org.orng.shelezyaka.core.domain.repositories.GameRepository
import org.orng.shelezyaka.core.domain.repositories.ResourceRepository
import org.orng.shelezyaka.core.domain.repositories.UserRepository
import org.orng.shelezyaka.core.domain.services.GameEngineService
import org.orng.shelezyaka.database.repositories.InMemoryGameRepository
import org.orng.shelezyaka.database.repositories.InMemoryResourceRepository
import org.orng.shelezyaka.database.repositories.InMemoryUserRepository

object DependencyConfig {

    // Репозитории
    private val userRepository: UserRepository by lazy { InMemoryUserRepository() }
    private val gameRepository: GameRepository by lazy { InMemoryGameRepository() }
    private val resourceRepository: ResourceRepository by lazy { InMemoryResourceRepository() }

    // Сервисы
    private val gameEngineService: GameEngineService by lazy {
        GameEngineService(gameRepository, resourceRepository, SimpleEventPublisher())
    }

    // Use cases
    val registerUser: RegisterUser by lazy {
        RegisterUser(userRepository) { password ->
            // Простое хеширование для примера
            password.hashCode().toString()
        }
    }

    val buyResource: BuyResource by lazy {
        BuyResource(gameRepository, resourceRepository)
    }

    val createGame: CreateGame by lazy {
        CreateGame(gameRepository, resourceRepository, lobbyRepository)
    }

    // Контроллеры
    val userController: UserController by lazy {
        UserController(registerUser, AuthenticateUser(userRepository))
    }

    val gameController: GameController by lazy {
        GameController(createGame, buyResource, gameEngineService)
    }
}