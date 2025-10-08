package org.orng.shelezyaka.core.usecases.game

import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.PlayerId
import org.orng.shelezyaka.core.domain.models.ResourceId
import org.orng.shelezyaka.core.domain.repositories.GameRepository
import org.orng.shelezyaka.core.domain.repositories.ResourceRepository

class BuyResource(
    private val gameRepository: GameRepository,
    private val resourceRepository: ResourceRepository
) {
    suspend operator fun invoke(
        gameId: GameId,
        playerId: PlayerId,
        resourceId: ResourceId,
        quantity: Int
    ): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        val player = game.players[playerId] ?:
        return OperationResult.Failure("Player not found")

        val resource = resourceRepository.findById(resourceId) ?:
        return OperationResult.Failure("Resource not found")

        // Проверяем доступность ресурса в игре
        if (!game.config.availableResourceIds.contains(resourceId)) {
            return OperationResult.Failure("Resource not available in this game")
        }

        // Рассчитываем цену
        val (buyPrice, _) = resourceRepository.getPrices(resourceId, game.currentDay)
        val totalCost = buyPrice * quantity

        // Проверяем возможность покупки
        if (!player.canAfford(totalCost)) {
            return OperationResult.Failure("Not enough money")
        }

        // Обновляем состояние игрока
        val currentQuantity = player.resources[resourceId] ?: 0
        val updatedPlayer = player.copy(
            money = player.money - totalCost,
            resources = player.resources + (resourceId to (currentQuantity + quantity))
        )

        return gameRepository.updatePlayerState(gameId, updatedPlayer)
    }
}