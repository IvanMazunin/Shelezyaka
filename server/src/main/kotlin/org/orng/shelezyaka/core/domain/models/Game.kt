package org.orng.shelezyaka.core.domain.models

import org.orng.shelezyaka.core.domain.models.Player
import java.util.UUID

enum class GamePhase {
    MORNING, DAY, EVENING
}

enum class GameStatus {
    ACTIVE, FINISHED
}

data class Game(
    val id: GameId,
    val lobbyId: LobbyId,
    val config: GameConfig,
    val currentDay: Int = 1,
    val phase: GamePhase = GamePhase.MORNING,
    val activeCatastrophes: List<Catastrophe> = emptyList(),
    val status: GameStatus = GameStatus.ACTIVE,
    val players: Map<PlayerId, Player>, // playerId -> Player
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isFinished(): Boolean = currentDay > config.totalDays || status == GameStatus.FINISHED
    fun getWinner(): Player? = if (isFinished()) players.values.maxByOrNull { it.money } else null

    fun canProceedToNextPhase(): Boolean {
        return when (phase) {
            GamePhase.MORNING -> players.values.all { it.hasTakenCredit }
            GamePhase.DAY -> true // Всегда можно перейти после катастрофы
            GamePhase.EVENING -> true // Всегда можно перейти после продажи
        }
    }
}