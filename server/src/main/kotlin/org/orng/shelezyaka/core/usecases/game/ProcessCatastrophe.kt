package org.orng.shelezyaka.core.usecases.game

import org.orng.shelezyaka.core.domain.models.Catastrophe
import org.orng.shelezyaka.core.domain.models.CatastrophePool
import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.events.CatastropheOccurredEvent
import org.orng.shelezyaka.core.domain.models.CatastropheId
import org.orng.shelezyaka.core.domain.services.GameEventService

class ProcessCatastrophe(
    private val catastrophePool: CatastrophePool,
    private val eventService: GameEventService
) {
    operator fun invoke(game: Game): Pair<Game, CatastropheOccurredEvent> {
        // Выбираем случайную катастрофу из доступных для этой игры
        val availableCatastrophes = catastrophePool.getCatastrophes(
            game.config.availableCatastropheIds
        )

        if (availableCatastrophes.isEmpty()) {
            return game to CatastropheOccurredEvent(
                catastrophe = Catastrophe(
                    id = CatastropheId("no_catastrophe"),
                    name = "Без катастрофы",
                    description = "Сегодня ничего не произошло",
                    effect = { g, _ -> g }
                ),
                gameId = game.id,
                severity = 0
            )
        }

        val randomCatastrophe = availableCatastrophes.random()
        val updatedGame = randomCatastrophe.effect(game, game.currentDay)

        val event = eventService.createCatastropheEvent(
            gameId = game.id,
            catastrophe = randomCatastrophe
        )

        return updatedGame to event
    }
}