package com.dcelysia.changli_planet_admin.feature.user.data.repository

import com.dcelysia.changli_planet_admin.core.network.ApiResponse
import com.dcelysia.changli_planet_admin.core.network.NetworkConfig
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import com.dcelysia.changli_planet_admin.feature.user.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class UserRepository {
    private val client = NetworkConfig.httpClient

    /**
     * 分页查询用户列表，支持多条件过滤
     */
    suspend fun getUsers(params: UserQueryParams = UserQueryParams()): Result<ApiResponse<List<UserFullInfo>>> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")

            println("UserRepository - Fetching users with params: $params")

            val response = client.get("${NetworkConfig.BASE_URL}/web/users") {
                header("token", token)
                parameter("page", params.page)
                parameter("limit", params.limit)
                params.userName?.let { parameter("userName", it) }
                params.isAdmin?.let { parameter("isAdmin", it) }
                params.isDeleted?.let { parameter("isDeleted", it) }
                params.isBanned?.let { parameter("isBanned", it) }
            }.body<ApiResponse<List<UserFullInfo>>>()

            println("UserRepository - Users fetched successfully: ${response.data.size} users")
            Result.success(response)
        } catch (e: Exception) {
            println("UserRepository - Error fetching users: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 更新用户信息（包括删除、封禁等操作）
     */
    suspend fun updateUser(request: UpdateUserRequest): Result<UpdateUserResponse> {
        return try {
            val token = TokenManager.getToken() ?: throw Exception("未登录")

            println("UserRepository - Updating user ${request.userReq.userId}")

            val response = client.put("${NetworkConfig.BASE_URL}/web/users") {
                header("token", token)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<UpdateUserResponse>()

            println("UserRepository - User updated successfully")
            Result.success(response)
        } catch (e: Exception) {
            println("UserRepository - Error updating user: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 辅助方法：封禁用户
     */
    suspend fun banUser(user: UserFullInfo): Result<UpdateUserResponse> {
        val request = createUpdateRequest(user, isBanned = 1)
        return updateUser(request)
    }

    /**
     * 辅助方法：解封用户
     */
    suspend fun unbanUser(user: UserFullInfo): Result<UpdateUserResponse> {
        val request = createUpdateRequest(user, isBanned = 0)
        return updateUser(request)
    }

    /**
     * 辅助方法：删除用户（软删除）
     */
    suspend fun deleteUser(user: UserFullInfo): Result<UpdateUserResponse> {
        val request = createUpdateRequest(user, isDeleted = 1)
        return updateUser(request)
    }

    /**
     * 辅助方法：恢复已删除的用户
     */
    suspend fun restoreUser(user: UserFullInfo): Result<UpdateUserResponse> {
        val request = createUpdateRequest(user, isDeleted = 0)
        return updateUser(request)
    }

    /**
     * 创建更新请求
     */
    fun createUpdateRequest(
        user: UserFullInfo,
        isDeleted: Int? = null,
        isBanned: Int? = null,
        isAdmin: Int? = null
    ): UpdateUserRequest {
        return UpdateUserRequest(
            userReq = UserReq(
                userId = user.userResp.userId,
                username = user.userResp.username,
                password = user.userResp.password,
                isAdmin = isAdmin ?: user.userResp.isAdmin,
                isDeleted = isDeleted ?: if (user.userResp.isDeleted) 1 else 0,
                isBanned = isBanned ?: if (user.userResp.isBanned) 1 else 0,
                description = user.userResp.description
            ),
            userProfileReq = UserProfileReq(
                userId = user.userProfileResp.userId,
                avatarUrl = user.userProfileResp.avatarUrl,
                bio = user.userProfileResp.bio,
                userLevel = user.userProfileResp.userLevel,
                gender = user.userProfileResp.gender,
                grade = user.userProfileResp.grade,
                birthDate = user.userProfileResp.birthDate,
                location = user.userProfileResp.location,
                website = user.userProfileResp.website,
                isDeleted = user.userProfileResp.isDeleted,
                description = user.userProfileResp.description
            ),
            userStatsReq = UserStatsReq(
                userId = user.userStatsResp.userId,
                studentNumber = user.userStatsResp.studentNumber,
                articleCount = user.userStatsResp.articleCount,
                commentCount = user.userStatsResp.commentCount,
                statementCount = user.userStatsResp.statementCount,
                likedCount = user.userStatsResp.likedCount,
                coinCount = user.userStatsResp.coinCount,
                xp = user.userStatsResp.xp,
                quizType = user.userStatsResp.quizType,
                lastLoginTime = user.userStatsResp.lastLoginTime,
                isDeleted = user.userStatsResp.isDeleted,
                description = user.userStatsResp.description
            )
        )
    }
}
