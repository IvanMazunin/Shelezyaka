package org.orng.shelezyaka.core.domain.models

import java.util.UUID

enum class LobbyStatus {
    WAITING, STARTED, FINISHED
}

data class Lobby(
    val id: LobbyId,
    val creatorId: UUID,
    val name: String,
    val config: GameConfig,
    val playerIds: Set<PlayerId>,
    val status: LobbyStatus = LobbyStatus.WAITING,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun canStart(): Boolean = playerIds.size >= 2 && status == LobbyStatus.WAITING
    fun canJoin(playerId: PlayerId): Boolean = status == LobbyStatus.WAITING && !playerIds.contains(playerId)
}