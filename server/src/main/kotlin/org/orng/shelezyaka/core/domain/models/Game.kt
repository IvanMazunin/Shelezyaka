package org.orng.shelezyaka.core.domain.models

import org.orng.shelezyaka.core.domain.models.Player
import java.util.UUID

enum class GamePhase {
    MORNING, DAY, EVENING
}

enum class GameStatus {
    WAITING, ACTIVE, FINISHED
}

data class Game(
    val id: GameId,
    val config: GameConfig,

    val currentDay: Int = 1,
    val phase: GamePhase = GamePhase.MORNING,

    val activeCatastrophes: List<Catastrophe> = emptyList(),

    val players: Map<PlayerId, Player>,
    val spectators: Map<PlayerId, Player>,

    val status: GameStatus = GameStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isFinished(): Boolean = currentDay > config.totalDays || status == GameStatus.FINISHED
    fun getWinner(): Player? = if (isFinished()) players.values.maxByOrNull { it.money } else null

}