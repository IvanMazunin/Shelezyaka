package org.orng.shelezyaka.database.repositories

import org.orng.shelezyaka.core.domain.models.Catastrophe
import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.GamePhase
import org.orng.shelezyaka.core.domain.models.LobbyId
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.Player
import org.orng.shelezyaka.core.domain.models.PlayerId
import org.orng.shelezyaka.core.domain.repositories.GameRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class InMemoryGameRepository : GameRepository {
    private val games = ConcurrentHashMap<GameId, Game>()
    private val idCounter = AtomicInteger(1)

    override suspend fun findById(id: GameId): Game? = games[id]

    override suspend fun findByLobby(lobbyId: LobbyId): Game? =
        games.values.find { it.lobbyId == lobbyId }

    override suspend fun findByPlayer(playerId: PlayerId): List<Game> =
        games.values.filter { it.players.containsKey(playerId) }

    override suspend fun save(game: Game): OperationResult<Game> {
        games[game.id] = game
        return OperationResult.Success(game)
    }

    override suspend fun updatePhase(gameId: GameId, phase: GamePhase): OperationResult<Game> {
        val game = games[gameId] ?: return OperationResult.Failure("Game not found")
        val updatedGame = game.copy(phase = phase)
        games[gameId] = updatedGame
        return OperationResult.Success(updatedGame)
    }

    override suspend fun nextDay(gameId: GameId): OperationResult<Game> {
        val game = games[gameId] ?: return OperationResult.Failure("Game not found")
        val updatedGame = game.copy(currentDay = game.currentDay + 1)
        games[gameId] = updatedGame
        return OperationResult.Success(updatedGame)
    }

    override suspend fun updatePlayerState(gameId: GameId, player: Player): OperationResult<Game> {
        val game = games[gameId] ?: return OperationResult.Failure("Game not found")
        val updatedPlayers = game.players.toMutableMap()
        updatedPlayers[player.id] = player
        val updatedGame = game.copy(players = updatedPlayers)
        games[gameId] = updatedGame
        return OperationResult.Success(updatedGame)
    }

    override suspend fun addCatastrophe(gameId: GameId, catastrophe: Catastrophe): OperationResult<Game> {
        val game = games[gameId] ?: return OperationResult.Failure("Game not found")
        val updatedCatastrophes = game.activeCatastrophes.toMutableList()
        updatedCatastrophes.add(catastrophe)
        val updatedGame = game.copy(activeCatastrophes = updatedCatastrophes)
        games[gameId] = updatedGame
        return OperationResult.Success(updatedGame)
    }

    override suspend fun getNextGameId(): Int = idCounter.getAndIncrement()
}