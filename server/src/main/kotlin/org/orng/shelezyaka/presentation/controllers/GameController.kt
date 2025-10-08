package org.orng.shelezyaka.presentation.controllers

import org.orng.shelezyaka.core.domain.models.LobbyId
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.repositories.LobbyRepository
import org.orng.shelezyaka.core.usecases.game.CreateGame
import org.orng.shelezyaka.core.usecases.game.ProcessCatastrophe

// presentation/src/main/kotlin/org/orng/shelezyaka/presentation/controllers/GameController.kt
class GameController(
    private val createGame: CreateGame,
    private val processCatastrophe: ProcessCatastrophe,
    private val getAvailableResources: GetAvailableResources,
    private val lobbyRepository: LobbyRepository
) {

    fun getResources(request: GetResourcesRequest): List<ResourceResponse> {
        val allResources = getAvailableResources()
        return allResources.map { it.toResponse() }
    }

    fun startGame(lobbyId: LobbyId): OperationResult<GameResponse> {
        val lobby = lobbyRepository.findById(lobbyId) ?:
        return OperationResult.Failure("Lobby not found")

        return when (val result = createGame(lobby)) {
            is OperationResult.Success -> {
                val game = result.data
                gameRepository.save(game)
                OperationResult.Success(game.toResponse())
            }
            is OperationResult.Failure -> result
        }
    }
}