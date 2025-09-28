package com.dcelysia.changli_planet_admin.core.network

import io.ktor.client.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

object NetworkConfig {
    const val BASE_URL = "http://113.44.47.220:8082"
    
    val httpClient by lazy {
        createHttpClient()
    }
    
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
}
