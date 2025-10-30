package org.orng.shelezyaka.presentation.websocket

import io.ktor.server.routing.Routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.json.Json
import org.orng.shelezyaka.core.domain.models.GameId
import org.orng.shelezyaka.core.domain.models.PlayerId
import org.orng.shelezyaka.core.domain.services.GamePhaseService

class GameWebSocketHandler(
    private val gamePhaseService: GamePhaseService,
    private val completePlayerAction: CompletePlayerAction
) {

    fun configureWebSocket(routing: Routing) {
        routing.webSocket("/api/game/{gameId}/ws") { session ->
            val gameId = call.parameters["gameId"]?.toInt()?.let { GameId(it) }
                ?: return@webSocket session.close(CloseReason(400, "Invalid game ID"))

            // Отправляем текущее состояние игры
            sendGameState(session, gameId)

            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val message = frame.readText()
                        handleWebSocketMessage(session, gameId, message)
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun handleWebSocketMessage(
        session: WebSocketSession,
        gameId: GameId,
        message: String
    ) {
        try {
            val actionMessage = Json.decodeFromString<WebSocketActionMessage>(message)

            when (actionMessage.type) {
                "player_ready" -> {
                    val playerId = PlayerId(actionMessage.playerId)
                    completePlayerAction(gameId, playerId, actionMessage.action)
                    // Рассылаем обновление всем подключенным клиентам
                    broadcastGameUpdate(gameId)
                }
                "get_state" -> {
                    sendGameState(session, gameId)
                }
            }
        } catch (e: Exception) {
            session.send(Frame.Text("""{"error": "${e.message}"}"""))
        }
    }

    private suspend fun sendGameState(session: WebSocketSession, gameId: GameId) {
        val game = gameRepository.findById(gameId) ?: return
        val phaseState = game.phaseState

        val stateMessage = WebSocketStateMessage(
            gameId = gameId.value,
            currentPhase = game.phase,
            day = game.currentDay,
            phaseStartTime = phaseState?.startTime,
            phaseDuration = phaseState?.durationMs,
            readyPlayers = phaseState?.playersReady?.map { it.value } ?: emptySet(),
            totalPlayers = game.players.size,
            timeRemaining = phaseState?.let {
                it.durationMs - (System.currentTimeMillis() - it.startTime)
            }
        )

        session.send(Frame.Text(Json.encodeToString(stateMessage)))
    }
}

data class WebSocketActionMessage(
    val type: String,
    val playerId: Int,
    val action: PlayerAction
)

data class WebSocketStateMessage(
    val gameId: Int,
    val currentPhase: GamePhase,
    val day: Int,
    val phaseStartTime: Long?,
    val phaseDuration: Long?,
    val readyPlayers: Set<Int>,
    val totalPlayers: Int,
    val timeRemaining: Long?
)