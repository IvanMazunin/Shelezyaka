package org.orng.shelezyaka.core.domain.repositories

import org.orng.shelezyaka.core.domain.models.Catastrophe
import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.GamePhase
import org.orng.shelezyaka.core.domain.models.LobbyId
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.Player
import org.orng.shelezyaka.core.domain.models.PlayerId

interface GameRepository {
    suspend fun findById(id: GameId): Game?
    suspend fun findByLobby(lobbyId: LobbyId): Game?
    suspend fun findByPlayer(playerId: PlayerId): List<Game>
    suspend fun save(game: Game): OperationResult<Game>
    suspend fun updatePhase(gameId: GameId, phase: GamePhase): OperationResult<Game>
    suspend fun nextDay(gameId: GameId): OperationResult<Game>
    suspend fun updatePlayerState(gameId: GameId, player: Player): OperationResult<Game>
    suspend fun addCatastrophe(gameId: GameId, catastrophe: Catastrophe): OperationResult<Game>
    suspend fun getNextGameId(): Int
}