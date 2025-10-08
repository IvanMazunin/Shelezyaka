package org.orng.shelezyaka.core.domain.services

import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.GamePhase
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.Player
import org.orng.shelezyaka.core.domain.repositories.GameRepository
import org.orng.shelezyaka.core.domain.repositories.ResourceRepository

class GameEngineService(
    private val gameRepository: GameRepository,
    private val resourceRepository: ResourceRepository,
) {

    suspend fun processMorningPhase(gameId: GameId): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        // Применяем эффекты ресурсов
        val updatedPlayers = game.players.mapValues { (_, player) ->
            applyResourceEffects(player, game.currentDay)
        }

        val updatedGame = game.copy(players = updatedPlayers)
        return gameRepository.save(updatedGame)
    }

    suspend fun processDayPhase(gameId: GameId): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        // Генерируем катастрофу (будет реализовано в use case)
        // Применяем эффекты катастрофы

        val updatedGame = game.copy(phase = GamePhase.EVENING)
        return gameRepository.save(updatedGame)
    }

    suspend fun processEveningPhase(gameId: GameId): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        // Выбираем случайный ресурс для продажи
        val randomResourceId = game.config.availableResourceIds.random()
        val (_, sellPrice) = resourceRepository.getPrices(
           randomResourceId,
            game.currentDay
        )

        // Обновляем деньги игроков
        val updatedPlayers = game.players.mapValues { (_, player) ->
            val quantity = player.resources[randomResourceId] ?: 0
            player.copy(
                money = player.money + (quantity * sellPrice),
                resources = player.resources - randomResourceId
            )
        }

        val updatedGame = game.copy(
            players = updatedPlayers,
            phase = if (game.currentDay >= game.config.totalDays) {
                GamePhase.MORNING
            } else {
                GamePhase.MORNING
            }
        )

        return gameRepository.save(updatedGame)
    }

    suspend fun applyResourceEffects(player: Player, currentDay: Int): Player {
        var updatedPlayer = player
        player.resources.keys.forEach { resourceId ->
            val resource = resourceRepository.findById(resourceId)
            resource?.effect?.invoke(updatedPlayer, currentDay)?.let {
                updatedPlayer = it
            }
        }
        return updatedPlayer
    }
}