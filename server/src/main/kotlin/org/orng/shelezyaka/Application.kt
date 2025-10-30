package org.orng.shelezyaka

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.orng.shelezyaka.plugins.configureHTTP
import org.orng.shelezyaka.plugins.configureRouting
import org.orng.shelezyaka.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val sessionService = GameSessionService()
        val gameEngine = GameEngineService(sessionService)

        configureRouting(sessionService, gameEngine)
        configureSerialization()
        configureMonitoring()
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}