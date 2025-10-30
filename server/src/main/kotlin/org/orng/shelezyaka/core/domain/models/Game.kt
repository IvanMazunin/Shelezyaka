package org.orng.shelezyaka.core.domain.models

import org.orng.shelezyaka.core.domain.models.Player
import java.util.UUID

enum class GameStatus {
    WAITING, ACTIVE, FINISHED
}

data class Game(
    val id: GameId,
    val name : String,
    val creatorId : UUID,

    val config: GameConfig,

    val currentDay: Int = 1,
    val phaseState: GamePhaseState? = null, // Текущее состояние фазы

    val activeCatastrophes: List<Catastrophe> = emptyList(),

    val players: Map<PlayerId, Player>,
    val spectators: Map<PlayerId, Player>,

    val status: GameStatus = GameStatus.WAITING,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isFinished(): Boolean = currentDay > config.totalDays || status == GameStatus.FINISHED
    fun getWinner(): Player? = if (isFinished()) players.values.maxByOrNull { it.money } else null

    fun initializePhaseState(phase: GamePhase, startTime: Long): Game {
        val durationMs = when (phase) {
            GamePhase.MORNING -> 30_000L // 30 секунд на покупку
            GamePhase.DAY -> 10_000L // 10 секунд на катастрофу (автоматически)
            GamePhase.EVENING -> 30_000L // 30 секунд на продажу
        }

        return copy(
            phase = phase,
            phaseState = GamePhaseState(
                phase = phase,
                startTime = startTime,
                durationMs = durationMs,
                playersReady = emptySet()
            )
        )
    }

    fun markPlayerReady(playerId: PlayerId): Game {
        val updatedState = phaseState?.markPlayerReady(playerId)
        return copy(phaseState = updatedState)
    }

    fun completePhase(): Game {
        return copy(phaseState = phaseState?.copy(phaseCompleted = true))
    }

}