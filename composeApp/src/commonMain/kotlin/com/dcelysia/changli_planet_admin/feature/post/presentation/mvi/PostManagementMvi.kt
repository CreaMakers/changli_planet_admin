package com.dcelysia.changli_planet_admin.feature.post.presentation.mvi

import com.dcelysia.changli_planet_admin.feature.post.data.model.PostItem

// MVI State
data class PostManagementUiState(
    val posts: List<PostItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val selectedTab: Int = 0 // 0: 全部, 1: 待审核, 2: 已拒绝
)

// MVI Intent
sealed class PostManagementIntent {
    object LoadPosts : PostManagementIntent()
    object RefreshPosts : PostManagementIntent()
    data class ChangeTab(val tabIndex: Int) : PostManagementIntent()
    data class ApprovePost(val postId: Int) : PostManagementIntent()
    data class RejectPost(val postId: Int) : PostManagementIntent()
    data class DeletePost(val postId: Int) : PostManagementIntent()
    object ClearError : PostManagementIntent()
}

// MVI Effect
sealed class PostManagementEffect {
    data class ShowMessage(val message: String) : PostManagementEffect()
    data class ShowError(val message: String) : PostManagementEffect()
}
