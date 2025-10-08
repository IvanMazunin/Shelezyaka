package org.orng.shelezyaka.core.domain.repositories

import org.orng.shelezyaka.core.domain.models.Lobby
import org.orng.shelezyaka.core.domain.models.LobbyId
import org.orng.shelezyaka.core.domain.models.LobbyStatus
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.UserId

interface LobbyRepository {
    suspend fun findById(id: LobbyId): Lobby?
    suspend fun findByCreator(creatorId: UserId): List<Lobby>
    suspend fun findByPlayer(playerId: UserId): List<Lobby>
    suspend fun save(lobby: Lobby): OperationResult<Lobby>
    suspend fun updateStatus(lobbyId: LobbyId, status: LobbyStatus): OperationResult<Lobby>
    suspend fun addPlayer(lobbyId: LobbyId, playerId: UserId): OperationResult<Lobby>
    suspend fun removePlayer(lobbyId: LobbyId, playerId: UserId): OperationResult<Lobby>
    suspend fun getNextLobbyId(): Int
}