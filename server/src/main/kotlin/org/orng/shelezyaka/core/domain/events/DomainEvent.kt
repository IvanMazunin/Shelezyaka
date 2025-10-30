package org.orng.shelezyaka.core.domain.events

import org.orng.shelezyaka.core.domain.models.Catastrophe
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.GamePhase
import org.orng.shelezyaka.core.domain.models.PlayerId
import org.orng.shelezyaka.core.domain.models.ResourceId

// Модели событий
sealed class GameEvent {
    abstract val gameId: GameId
    abstract val timestamp: Long
}

data class ResourceTradedEvent(
    val playerId: PlayerId,
    val resourceId: ResourceId,
    val action: ResourceAction,
    val quantity: Int,
    val price: Double,
    val totalCost: Double,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

data class CatastropheOccurredEvent(
    val catastrophe: Catastrophe,
    val severity: Int,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

data class PhaseTransitionEvent(
    val fromPhase: GamePhase,
    val toPhase: GamePhase,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

data class PhaseStartedEvent(
    val phase: GamePhase,
    val day: Int,
    val durationMs: Long,
    val startTime: Long,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

data class PlayerReadyEvent(
    val playerId: PlayerId,
    val phase: GamePhase,
    val readyPlayers: Set<PlayerId>,
    val totalPlayers: Int,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

data class PhaseTimeWarningEvent(
    val phase: GamePhase,
    val remainingMs: Long,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

data class PhaseCompletedEvent(
    val phase: GamePhase,
    val completedBy: PhaseCompletionReason,
    val nextPhase: GamePhase?,
    override val gameId: GameId,
    override val timestamp: Long = System.currentTimeMillis()
) : GameEvent()

enum class PhaseCompletionReason {
    ALL_PLAYERS_READY, TIME_EXPIRED, MANUAL_COMPLETION
}

enum class ResourceAction { BUY, SELL }