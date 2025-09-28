package com.dcelysia.changli_planet_admin.feature.auth.data.repository

import com.dcelysia.changli_planet_admin.core.network.NetworkConfig
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import com.dcelysia.changli_planet_admin.feature.auth.data.model.LoginRequest
import com.dcelysia.changli_planet_admin.feature.auth.data.model.LoginResponse
import io.ktor.client.call.*
import io.ktor.client.request.*

class AuthRepository() {
    private val client = NetworkConfig.httpClient
    
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("${NetworkConfig.BASE_URL}/web/users/login") {
                setBody(LoginRequest(username, password))
            }.body<LoginResponse>()
            
            if (response.code == "200") {
                TokenManager.saveToken(response.data.access_token)
                TokenManager.saveUsername(username)
            }
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        TokenManager.clearAll()
    }
    
    fun isLoggedIn(): Boolean = TokenManager.isLoggedIn()
    
    fun getCurrentUser(): String? = TokenManager.getUsername()
}
