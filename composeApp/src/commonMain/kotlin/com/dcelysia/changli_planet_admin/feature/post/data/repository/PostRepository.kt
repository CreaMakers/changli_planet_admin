package com.dcelysia.changli_planet_admin.feature.post.data.repository

import com.dcelysia.changli_planet_admin.core.network.ApiNotDataResponse
import com.dcelysia.changli_planet_admin.core.network.NetworkConfig
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import com.dcelysia.changli_planet_admin.feature.post.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class PostRepository {
    private val client = NetworkConfig.httpClient

    /**
     * 分页查询新鲜事列表
     */
    suspend fun getPosts(params: PostQueryParams = PostQueryParams()): Result<FreshNewsResponse> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")
            
            println("PostRepository - Fetching posts with params: $params")

            val response = client.get("${NetworkConfig.BASE_URL}/web/fresh_news/check/image_query") {
                header("token", token)
                parameter("page", params.page)
                parameter("pageSize", params.pageSize)
                params.checkStatus?.let { parameter("checkStatus", it) }
            }.body<FreshNewsResponse>()
            
            println("PostRepository - Posts fetched successfully: ${response.data.size} posts")
            Result.success(response)
        } catch (e: Exception) {
            println("PostRepository - Error fetching posts: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * 审核新鲜事
     */
    suspend fun reviewPost(params: ReviewParams): Result<ApiNotDataResponse> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")
            
            println("PostRepository - Reviewing post ${params.freshNewsCheckId} with status ${params.checkStatus}")
            
            val response = client.put("${NetworkConfig.BASE_URL}/web/fresh_news/check/image") {
                header("token", token)
                parameter("freshNewsCheckId", params.freshNewsCheckId)
                parameter("checkStatus", params.checkStatus)
            }.body<ApiNotDataResponse>()
            
            println("PostRepository - Post reviewed successfully")
            Result.success(response)
        } catch (e: Exception) {
            println("PostRepository - Error reviewing post: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * 通过审核
     */
    suspend fun approvePost(freshNewsCheckId: Int): Result<ApiNotDataResponse> {
        return reviewPost(ReviewParams(freshNewsCheckId, 1))
    }
    
    /**
     * 拒绝审核
     */
    suspend fun rejectPost(freshNewsCheckId: Int): Result<ApiNotDataResponse> {
        return reviewPost(ReviewParams(freshNewsCheckId, 2))
    }
}
