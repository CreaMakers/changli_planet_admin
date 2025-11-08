package com.dcelysia.changli_planet_admin.feature.post.presentation.mvi

import com.dcelysia.changli_planet_admin.feature.post.data.model.FreshNews

// MVI State
data class PostManagementUiState(
    val posts: List<FreshNews> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    
    // 分页相关
    val currentPage: Int = 1,
    val pageSize: Int = 10,
    val hasMore: Boolean = true,
    
    // 过滤条件
    val selectedTab: Int = 0,  // 0：待审核，1：已通过，2：已拒绝
    
    // 详情预览
    val previewPost: FreshNews? = null,
    val isPreviewDialogOpen: Boolean = false
)

// MVI Intent
sealed class PostManagementIntent {
    object LoadPosts : PostManagementIntent()
    object RefreshPosts : PostManagementIntent()
    object LoadMorePosts : PostManagementIntent()
    
    // 标签切换
    data class SelectTab(val tabIndex: Int) : PostManagementIntent()
    
    // 审核操作
    data class ApprovePost(val post: FreshNews) : PostManagementIntent()
    data class RejectPost(val post: FreshNews) : PostManagementIntent()
    
    // 预览
    data class OpenPreview(val post: FreshNews) : PostManagementIntent()
    object ClosePreview : PostManagementIntent()
    
    object ClearError : PostManagementIntent()
}

// MVI Effect
sealed class PostManagementEffect {
    data class ShowMessage(val message: String) : PostManagementEffect()
    data class ShowError(val message: String) : PostManagementEffect()
    object ScrollToTop : PostManagementEffect()
}
