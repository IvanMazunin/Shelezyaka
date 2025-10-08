package org.orng.shelezyaka.core.usecases.game

import org.orng.shelezyaka.core.domain.models.CatastropheId
import org.orng.shelezyaka.core.domain.models.CatastrophePool
import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.Lobby
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.Player
import org.orng.shelezyaka.core.domain.models.ResourcePool
import org.orng.shelezyaka.core.domain.repositories.GameRepository
import java.util.UUID

class CreateGame(
    private val gameRepository: GameRepository,
    private val resourcePool: ResourcePool,
    private val catastrophePool: CatastrophePool
) {
    operator fun invoke(lobby: Lobby): OperationResult<Game> {
        // Валидация выбранных ресурсов
        val availableResources = resourcePool.getResources(lobby.config.availableResourceIds)
        if (availableResources.size != lobby.config.availableResourceIds.size) {
            return OperationResult.Failure("Some selected resources are not available")
        }

        // Фильтрация катастроф по доступным ресурсам
        val availableCatastrophes = catastrophePool
            .getAvailableCatastrophes(lobby.config.availableResourceIds)
            .map { it.id }
            .toSet()

        // Создание начального состояния игроков
        val players = lobby.playerIds.associateWith { playerId ->
            Player(
                id = playerId,
                username = "Player_$playerId", // В реальности будем брать из репозитория
                money = lobby.config.startMoney,
                resources = emptyMap(),
                credit = 0.0
            )
        }

        val game = Game(
            id = GameId(UUID.randomUUID()),
            lobbyId = lobby.id,
            config = lobby.config.copy(
                availableCatastropheIds = availableCatastrophes
            ),
            players = players
        )

        return OperationResult.Success(game)
    }
}