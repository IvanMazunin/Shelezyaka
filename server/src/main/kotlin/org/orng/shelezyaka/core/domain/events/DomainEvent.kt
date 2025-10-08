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

enum class ResourceAction { BUY, SELL }