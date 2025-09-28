package com.dcelysia.changli_planet_admin.feature.user.data.repository

import com.dcelysia.changli_planet_admin.core.network.NetworkConfig
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import com.dcelysia.changli_planet_admin.feature.user.data.model.UsersResponse
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserRepository() {
    private val client = NetworkConfig.httpClient
    
    suspend fun getUsers(): Result<UsersResponse> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")
            
            val response = client.get("${NetworkConfig.BASE_URL}/web/users") {
                header("token", token)
            }.body<UsersResponse>()
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun banUser(userId: Int): Result<Unit> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")
            
            client.post("${NetworkConfig.BASE_URL}/web/users/$userId/ban") {
                header("token", token)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unbanUser(userId: Int): Result<Unit> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")
            
            client.post("${NetworkConfig.BASE_URL}/web/users/$userId/unban") {
                header("token", token)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")
            
            client.delete("${NetworkConfig.BASE_URL}/web/users/$userId") {
                header("token", token)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
