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
    val description: String?
)

@Serializable
data class UserProfileResp(
    val userId: Int,
    val avatarUrl: String?,
    val bio: String?,
    val userLevel: Int,
    val gender: Int,
    val grade: String?,
    val birthDate: String?,
    val location: String?,
    val website: String?,
    val createTime: String,
    val updateTime: String,
    val isDeleted: Int,
    val description: String?
)

@Serializable
data class UserStatsResp(
    val userId: Int,
    val studentNumber: String?,
    val articleCount: Int,
    val commentCount: Int,
    val statementCount: Int,
    val likedCount: Int,
    val coinCount: Int,
    val xp: Int,
    val quizType: Int,
    val lastLoginTime: String?,
    val createTime: String,
    val updateTime: String,
    val isDeleted: Int,
    val description: String?
)

@Serializable
data class UserFullInfo(
    val userResp: UserResp,
    val userProfileResp: UserProfileResp,
    val userStatsResp: UserStatsResp
)

// 用户编辑请求模型
@Serializable
data class UserReq(
    val userId: Int,
    val username: String,
    val password: String,
    val isAdmin: Int,
    val isDeleted: Int,
    val isBanned: Int,
    val description: String?
)

@Serializable
data class UserProfileReq(
    val userId: Int,
    val avatarUrl: String?,
    val bio: String?,
    val userLevel: Int,
    val gender: Int,
    val grade: String?,
    val birthDate: String?,
    val location: String?,
    val website: String?,
    val isDeleted: Int,
    val description: String?
)

@Serializable
data class UserStatsReq(
    val userId: Int,
    val studentNumber: String?,
    val articleCount: Int,
    val commentCount: Int,
    val statementCount: Int,
    val likedCount: Int,
    val coinCount: Int,
    val xp: Int,
    val quizType: Int,
    val lastLoginTime: String?,
    val isDeleted: Int,
    val description: String?
)

@Serializable
data class UpdateUserRequest(
    val userReq: UserReq,
    val userProfileReq: UserProfileReq,
    val userStatsReq: UserStatsReq
)


// 分页查询参数
data class UserQueryParams(
    val page: Int = 1,
    val limit: Int = 10,
    val userName: String? = null,
    val isAdmin: Boolean? = null,
    val isDeleted: Boolean? = null,
    val isBanned: Boolean? = null
)
