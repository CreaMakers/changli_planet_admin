package com.dcelysia.changli_planet_admin.feature.post.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostItem(
    val id: Int,
    val title: String,
    val content: String,
    val author: String,
    val createTime: String,
    val status: String,
    val likeCount: Int,
    val commentCount: Int,
    val userId: Int
)

enum class PostStatus {
    PUBLISHED, PENDING, REJECTED;
    
    companion object {
        fun fromString(status: String): PostStatus {
            return when (status.lowercase()) {
                "published" -> PUBLISHED
                "pending" -> PENDING  
                "rejected" -> REJECTED
                else -> PENDING
            }
        }
    }
}

@Serializable
data class PostsResponse(
    val code: String,
    val msg: String,
    val data: List<PostItem>
)
