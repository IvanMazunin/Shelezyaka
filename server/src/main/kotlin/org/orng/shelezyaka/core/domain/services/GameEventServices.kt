package org.orng.shelezyaka.core.domain.services

import org.orng.shelezyaka.core.domain.events.CatastropheOccurredEvent
import org.orng.shelezyaka.core.domain.events.PhaseTransitionEvent
import org.orng.shelezyaka.core.domain.events.ResourceAction
import org.orng.shelezyaka.core.domain.events.ResourceTradedEvent
import org.orng.shelezyaka.core.domain.models.Catastrophe
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.GamePhase
import org.orng.shelezyaka.core.domain.models.PlayerId
import org.orng.shelezyaka.core.domain.models.ResourceId

class GameEventService {

    fun createResourceEvent(
        gameId: GameId,
        playerId: PlayerId,
        resourceId: ResourceId,
        action: ResourceAction,
        quantity: Int,
        price: Double
    ): ResourceTradedEvent {
        return ResourceTradedEvent(
            gameId = gameId,
            playerId = playerId,
            resourceId = resourceId,
            action = action,
            quantity = quantity,
            price = price,
            totalCost = quantity * price
        )
    }

    fun createCatastropheEvent(
        gameId: GameId,
        catastrophe: Catastrophe
    ): CatastropheOccurredEvent {
        return CatastropheOccurredEvent(
            gameId = gameId,
            catastrophe = catastrophe,
            severity = calculateSeverity(catastrophe)
        )
    }

    fun createPhaseTransitionEvent(
        gameId: GameId,
        fromPhase: GamePhase,
        toPhase: GamePhase
    ): PhaseTransitionEvent {
        return PhaseTransitionEvent(
            gameId = gameId,
            fromPhase = fromPhase,
            toPhase = toPhase,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun calculateSeverity(catastrophe: Catastrophe): Int {
        return when (catastrophe.id.value) {
            "money_halving" -> 8
            "credit_forgiveness" -> 6
            else -> 5
        }
    }
}