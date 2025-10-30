package org.orng.shelezyaka.core.domain.services

import org.orng.shelezyaka.core.domain.models.Game
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.GamePhase
import org.orng.shelezyaka.core.domain.models.OperationResult
import org.orng.shelezyaka.core.domain.models.Player
import org.orng.shelezyaka.core.domain.repositories.GameRepository
import org.orng.shelezyaka.core.domain.repositories.ResourceRepository

class GameEngineService(private val sessionService: GameSessionService) {

    fun processMorningPhase(sessionId: String): GameSession? {
        val session = sessionService.getSession(sessionId) ?: return null

        // Применяем эффекты ресурсов и кредитов
        session.players.filter { it.role == PlayerRole.PLAYER }.forEach { player ->
            // Начисляем проценты по кредиту
            if (player.credit > 0) {
                player.credit += (player.credit * player.creditRate) / 100
            }
        }

        session.currentPhase = GamePhase.DAY
        return session
    }

    fun processDayPhase(sessionId: String): Disaster? {
        val session = sessionService.getSession(sessionId) ?: return null

        if (!session.disastersEnabled) {
            session.currentPhase = GamePhase.EVENING
            return null
        }

        val disaster = sessionService.getRandomDisaster()
        disaster.effect(session)

        session.currentPhase = GamePhase.EVENING
        return disaster
    }

    fun processEveningPhase(sessionId: String): ResourceType? {
        val session = sessionService.getSession(sessionId) ?: return null

        val sellResource = session.availableResources.random()
        session.market.sellResource = sellResource
        session.market.sellPrice = (session.market.buyPrices[sellResource] ?: 10) * 2

        // Переход к следующему дню
        if (session.currentDay >= session.totalDays) {
            endGame(sessionId)
        } else {
            session.currentDay++
            session.currentPhase = GamePhase.MORNING
        }

        return sellResource
    }

    fun playerBuyResource(sessionId: String, playerId: String, resource: ResourceType, quantity: Int): Boolean {
        val session = sessionService.getSession(sessionId) ?: return false
        val player = session.players.find { it.id == playerId && it.role == PlayerRole.PLAYER } ?: return false

        val price = session.market.buyPrices[resource] ?: return false
        val totalCost = price * quantity

        if (player.credit >= totalCost) {
            player.credit -= totalCost
            player.resources[resource] = (player.resources[resource] ?: 0) + quantity
            return true
        }

        return false
    }

    fun playerSellResource(sessionId: String, playerId: String, quantity: Int): Boolean {
        val session = sessionService.getSession(sessionId) ?: return false
        val player = session.players.find { it.id == playerId && it.role == PlayerRole.PLAYER } ?: return false
        val sellResource = session.market.sellResource ?: return false

        val currentQuantity = player.resources[sellResource] ?: 0
        if (currentQuantity >= quantity) {
            player.resources[sellResource] = currentQuantity - quantity
            player.credit += session.market.sellPrice * quantity
            return true
        }

        return false
    }

    private fun endGame(sessionId: String) {
        val session = sessionService.getSession(sessionId) ?: return
        session.isActive = false
        // Здесь можно добавить логику подсчета очков и определения победителя
    }
}