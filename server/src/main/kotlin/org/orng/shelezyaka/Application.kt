package org.orng.shelezyaka

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.orng.shelezyaka.plugins.configureHTTP
import org.orng.shelezyaka.plugins.configureRouting
import org.orng.shelezyaka.plugins.configureSerialization

fun main() {
    /*Database.connect(
        url = System.getenv("DATABASE_CONNECTION_STRING"),
        driver = "org.postgresql.Driver",
        user = System.getenv("POSTGRES_USER"),
        password = System.getenv("POSTGRES_PASSWORD")
    )*/

    embeddedServer(
        Netty,
        port = System.getenv("SERVER_PORT").toInt(),
        module = Application::shelezyakaModule
    ).start(wait = true)
}

fun Application.shelezyakaModule() {
    configureHTTP()
    configureRouting()
    configureSerialization()
}