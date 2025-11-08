package com.dcelysia.changli_planet_admin.core.network

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(Js) {
        install(ContentNegotiation) {
            json(NetworkConfig.json)
        }

        install(Logging) {
            level = LogLevel.INFO
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }
}
