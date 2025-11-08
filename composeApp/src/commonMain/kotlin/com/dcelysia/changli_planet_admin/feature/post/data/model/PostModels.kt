package com.dcelysia.changli_planet_admin.feature.post.data.model

import kotlinx.serialization.Serializable

/**
 * 新鲜事数据模型
 */
@Serializable
data class FreshNews(
    val freshNewsCheckId: Int,
    val freshNewsId: Int,
    val title: String,
    val content: String,
    val imageUrl: String,  // 多个图片用逗号分隔
    val checkStatus: Int,  // 0：待审核，1：通过，2：拒绝
    val createTime: String,
    val updateTime: String,
    val isDeleted: Int,
    val checkTime: String?
) {
    /**
     * 获取图片URL列表
     */
    fun getImageUrls(): List<String> {
        return imageUrl.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    
    /**
     * 获取审核状态文本
     */
    fun getCheckStatusText(): String {
        return when (checkStatus) {
            0 -> "待审核"
            1 -> "已通过"
            2 -> "已拒绝"
            else -> "未知"
        }
    }
}

/**
 * 新鲜事响应模型
 */
@Serializable
data class FreshNewsResponse(
    val code: String,
    val msg: String,
    val data: List<FreshNews>
)

/**
 * 审核请求参数
 */
data class ReviewParams(
    val freshNewsCheckId: Int,
    val checkStatus: Int  // 1：通过，2：拒绝
)

/**
 * 分页查询参数
 */
data class PostQueryParams(
    val page: Int = 1,
    val pageSize: Int = 10,
    val checkStatus: Int? = 0  // 默认只显示待审核的
)
