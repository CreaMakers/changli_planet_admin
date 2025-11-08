package com.dcelysia.changli_planet_admin.core.network

import io.ktor.client.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

object NetworkConfig {
    const val BASE_URL = "https://web.csust.creamaker.cn"
    
    val httpClient by lazy {
        createHttpClient()
    }
    
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
}
