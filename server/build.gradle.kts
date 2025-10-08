plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "org.orng.shelezyaka"
version = "1.0.0"
application {
    mainClass.set("org.orng.shelezyaka.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(project(":shared"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
}