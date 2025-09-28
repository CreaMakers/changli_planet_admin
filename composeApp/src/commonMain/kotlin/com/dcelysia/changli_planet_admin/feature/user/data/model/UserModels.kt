package com.dcelysia.changli_planet_admin.feature.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResp(
    val userId: Int,
    val username: String,
    val password: String,
    val isAdmin: Int,
    val isDeleted: Boolean,
    val isBanned: Boolean,
    val createTime: String,
    val updateTime: String,
    val description: String
)

@Serializable
data class UserProfileResp(
    val userId: Int,
    val avatarUrl: String?,
    val bio: String?,
    val userLevel: Int,
    val gender: Int,
    val grade: String,
    val birthDate: String,
    val location: String?,
    val website: String?,
    val createTime: String,
    val updateTime: String,
    val isDeleted: Int,
    val description: String
)

@Serializable
data class UserStatsResp(
    val userId: Int,
    val studentNumber: String,
    val articleCount: Int,
    val commentCount: Int,
    val statementCount: Int,
    val likedCount: Int,
    val coinCount: Int,
    val xp: Int,
    val quizType: Int,
    val lastLoginTime: String,
    val createTime: String,
    val updateTime: String,
    val isDeleted: Int,
    val description: String
)

@Serializable
data class UserFullInfo(
    val userResp: UserResp,
    val userProfileResp: UserProfileResp,
    val userStatsResp: UserStatsResp
)

@Serializable
data class UsersResponse(
    val code: String,
    val msg: String,
    val data: List<UserFullInfo>
)
