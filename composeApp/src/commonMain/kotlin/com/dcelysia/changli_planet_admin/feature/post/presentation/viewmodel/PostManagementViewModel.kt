package com.dcelysia.changli_planet_admin.feature.post.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dcelysia.changli_planet_admin.feature.post.data.model.PostQueryParams
import com.dcelysia.changli_planet_admin.feature.post.data.repository.PostRepository
import com.dcelysia.changli_planet_admin.feature.post.presentation.mvi.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PostManagementViewModel(
    private val postRepository: PostRepository = PostRepository()
) : ViewModel() {
    
    var uiState by mutableStateOf(PostManagementUiState())
        private set
        
    private val _effects = Channel<PostManagementEffect>()
    val effects = _effects.receiveAsFlow()
    
    init {
        println("PostManagementViewModel - Initialized")
        handleIntent(PostManagementIntent.LoadPosts)
    }
    
    fun handleIntent(intent: PostManagementIntent) {
        println("PostManagementViewModel - Handling intent: $intent")
        when (intent) {
            is PostManagementIntent.LoadPosts -> loadPosts()
            is PostManagementIntent.RefreshPosts -> refreshPosts()
            is PostManagementIntent.LoadMorePosts -> loadMorePosts()
            is PostManagementIntent.SelectTab -> selectTab(intent.tabIndex)
            is PostManagementIntent.ApprovePost -> approvePost(intent.post)
            is PostManagementIntent.RejectPost -> rejectPost(intent.post)
            is PostManagementIntent.OpenPreview -> {
                uiState = uiState.copy(previewPost = intent.post, isPreviewDialogOpen = true)
            }
            is PostManagementIntent.ClosePreview -> {
                uiState = uiState.copy(previewPost = null, isPreviewDialogOpen = false)
            }
            is PostManagementIntent.ClearError -> {
                uiState = uiState.copy(errorMessage = null)
            }
        }
    }
    
    private fun loadPosts(resetPage: Boolean = true) {
        if (resetPage) {
            uiState = uiState.copy(currentPage = 1, isLoading = true, errorMessage = null)
        }
        
        viewModelScope.launch {
            val checkStatus = when (uiState.selectedTab) {
                0 -> 0  // 待审核
                1 -> 1  // 已通过
                2 -> 2  // 已拒绝
                else -> 0
            }
            
            val params = PostQueryParams(
                page = uiState.currentPage,
                pageSize = uiState.pageSize,
                checkStatus = checkStatus
            )
            
            postRepository.getPosts(params)
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            posts = if (resetPage) response.data else uiState.posts + response.data,
                            isLoading = false,
                            hasMore = response.data.size >= uiState.pageSize
                        )
                        if (resetPage) {
                            _effects.send(PostManagementEffect.ScrollToTop)
                        }
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = response.msg
                        )
                        _effects.send(PostManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    val errorMessage = "加载帖子失败: ${error.message}"
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                    _effects.send(PostManagementEffect.ShowError(errorMessage))
                }
        }
    }
    
    private fun loadMorePosts() {
        if (uiState.isLoading || !uiState.hasMore) return
        
        uiState = uiState.copy(currentPage = uiState.currentPage + 1)
        loadPosts(resetPage = false)
    }
    
    private fun refreshPosts() {
        uiState = uiState.copy(isRefreshing = true, currentPage = 1)
        
        viewModelScope.launch {
            val checkStatus = when (uiState.selectedTab) {
                0 -> 0
                1 -> 1
                2 -> 2
                else -> 0
            }
            
            val params = PostQueryParams(
                page = 1,
                pageSize = uiState.pageSize,
                checkStatus = checkStatus
            )
            
            postRepository.getPosts(params)
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            posts = response.data,
                            isRefreshing = false,
                            hasMore = response.data.size >= uiState.pageSize
                        )
                        _effects.send(PostManagementEffect.ShowMessage("刷新成功"))
                    } else {
                        uiState = uiState.copy(
                            isRefreshing = false,
                            errorMessage = response.msg
                        )
                        _effects.send(PostManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    uiState = uiState.copy(isRefreshing = false)
                    _effects.send(PostManagementEffect.ShowError("刷新失败: ${error.message}"))
                }
        }
    }
    
    private fun selectTab(tabIndex: Int) {
        if (uiState.selectedTab == tabIndex) return
        
        uiState = uiState.copy(selectedTab = tabIndex, currentPage = 1)
        loadPosts(resetPage = true)
    }
    
    private fun approvePost(post: com.dcelysia.changli_planet_admin.feature.post.data.model.FreshNews) {
        viewModelScope.launch {
            postRepository.approvePost(post.freshNewsCheckId)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(PostManagementEffect.ShowMessage("已通过审核"))
                        refreshPosts()
                    } else {
                        _effects.send(PostManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(PostManagementEffect.ShowError("审核失败: ${error.message}"))
                }
        }
    }
    
    private fun rejectPost(post: com.dcelysia.changli_planet_admin.feature.post.data.model.FreshNews) {
        viewModelScope.launch {
            postRepository.rejectPost(post.freshNewsCheckId)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(PostManagementEffect.ShowMessage("已拒绝"))
                        refreshPosts()
                    } else {
                        _effects.send(PostManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(PostManagementEffect.ShowError("操作失败: ${error.message}"))
                }
        }
    }
}
