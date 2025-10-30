package org.orng.shelezyaka.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    sessionService: GameSessionService,
    gameEngine: GameEngineService
) {
    install(WebSockets)

    routing {
        route("/api/sessions") {
            post("/create") {
                val request = call.receive<CreateSessionRequest>()
                val session = sessionService.createSession(request)
                call.respond(session)
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respondText("Session ID required", status = HttpStatusCode.BadRequest)
                val session = sessionService.getSession(id)
                session?.let { call.respond(it) } ?: call.respondText("Session not found", status = HttpStatusCode.NotFound)
            }

            post("/{id}/join") {
                val sessionId = call.parameters["id"] ?: return@post call.respondText("Session ID required", status = HttpStatusCode.BadRequest)
                val request = call.receive<JoinSessionRequest>()
                val player = sessionService.joinSession(sessionId, request.playerName, request.role)
                player?.let { call.respond(it) } ?: call.respondText("Cannot join session", status = HttpStatusCode.BadRequest)
            }

            post("/{id}/start") {
                val sessionId = call.parameters["id"] ?: return@post call.respondText("Session ID required", status = HttpStatusCode.BadRequest)
                val success = sessionService.startGame(sessionId)
                if (success) call.respond(mapOf("status" to "started"))
                else call.respondText("Cannot start game", status = HttpStatusCode.BadRequest)
            }
        }

        route("/api/game") {
            post("/{sessionId}/morning") {
                val sessionId = call.parameters["sessionId"] ?: return@post call.respondText("Session ID required")
                val session = gameEngine.processMorningPhase(sessionId)
                session?.let { call.respond(it) } ?: call.respondText("Session not found")
            }

            post("/{sessionId}/day") {
                val sessionId = call.parameters["sessionId"] ?: return@post call.respondText("Session ID required")
                val disaster = gameEngine.processDayPhase(sessionId)
                call.respond(mapOf("disaster" to disaster))
            }

            post("/{sessionId}/evening") {
                val sessionId = call.parameters["sessionId"] ?: return@post call.respondText("Session ID required")
                val sellResource = gameEngine.processEveningPhase(sessionId)
                call.respond(mapOf("sellResource" to sellResource))
            }

            post("/{sessionId}/buy") {
                val sessionId = call.parameters["sessionId"] ?: return@post call.respondText("Session ID required")
                val request = call.receive<BuyRequest>()
                val success = gameEngine.playerBuyResource(sessionId, request.playerId, request.resource, request.quantity)
                call.respond(mapOf("success" to success))
            }

            post("/{sessionId}/sell") {
                val sessionId = call.parameters["sessionId"] ?: return@post call.respondText("Session ID required")
                val request = call.receive<SellRequest>()
                val success = gameEngine.playerSellResource(sessionId, request.playerId, request.quantity)
                call.respond(mapOf("success" to success))
            }
        }

        webSocket("/ws/game/{sessionId}") {
            val sessionId = call.parameters["sessionId"] ?: return@webSocket close(CloseReason(CloseReason.Codes.BAD_REQUEST, "No session ID"))
            // Реализация WebSocket для реального времени
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    // Обработка WebSocket сообщений
                }
            }
        }
    }
}