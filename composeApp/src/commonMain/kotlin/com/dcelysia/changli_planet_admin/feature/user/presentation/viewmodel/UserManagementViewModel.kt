package com.dcelysia.changli_planet_admin.feature.user.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dcelysia.changli_planet_admin.feature.user.data.model.UserQueryParams
import com.dcelysia.changli_planet_admin.feature.user.data.repository.UserRepository
import com.dcelysia.changli_planet_admin.feature.user.presentation.mvi.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    var uiState by mutableStateOf(UserManagementUiState())
        private set

    private val _effects = Channel<UserManagementEffect>()
    val effects = _effects.receiveAsFlow()

    init {
        println("UserManagementViewModel - Initialized")
        handleIntent(UserManagementIntent.LoadUsers)
    }

    fun handleIntent(intent: UserManagementIntent) {
        println("UserManagementViewModel - Handling intent: $intent")
        when (intent) {
            is UserManagementIntent.LoadUsers -> loadUsers()
            is UserManagementIntent.RefreshUsers -> refreshUsers()
            is UserManagementIntent.LoadMoreUsers -> loadMoreUsers()

            // 搜索和过滤
            is UserManagementIntent.UpdateSearchQuery -> {
                uiState = uiState.copy(searchQuery = intent.query)
            }
            is UserManagementIntent.UpdateAdminFilter -> {
                uiState = uiState.copy(filterAdmin = intent.isAdmin)
            }
            is UserManagementIntent.UpdateDeletedFilter -> {
                uiState = uiState.copy(filterDeleted = intent.isDeleted)
            }
            is UserManagementIntent.UpdateBannedFilter -> {
                uiState = uiState.copy(filterBanned = intent.isBanned)
            }
            is UserManagementIntent.ToggleFilterExpanded -> {
                uiState = uiState.copy(isFilterExpanded = !uiState.isFilterExpanded)
            }
            is UserManagementIntent.ApplyFilters -> applyFilters()
            is UserManagementIntent.ClearFilters -> clearFilters()

            // 用户操作
            is UserManagementIntent.BanUser -> banUser(intent.user)
            is UserManagementIntent.UnbanUser -> unbanUser(intent.user)
            is UserManagementIntent.DeleteUser -> deleteUser(intent.user)
            is UserManagementIntent.RestoreUser -> restoreUser(intent.user)
            is UserManagementIntent.OpenEditDialog -> {
                uiState = uiState.copy(editingUser = intent.user, isEditDialogOpen = true)
            }
            is UserManagementIntent.CloseEditDialog -> {
                uiState = uiState.copy(editingUser = null, isEditDialogOpen = false)
            }
            is UserManagementIntent.SaveUser -> saveUser(intent.user)

            is UserManagementIntent.ClearError -> {
                uiState = uiState.copy(errorMessage = null)
            }
        }
    }

    private fun loadUsers(resetPage: Boolean = true) {
        if (resetPage) {
            uiState = uiState.copy(currentPage = 1, isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            val params = UserQueryParams(
                page = uiState.currentPage,
                limit = uiState.pageSize,
                userName = if (uiState.searchQuery.isNotBlank()) uiState.searchQuery else null,
                isAdmin = uiState.filterAdmin,
                isDeleted = uiState.filterDeleted,
                isBanned = uiState.filterBanned
            )

            userRepository.getUsers(params)
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            users = if (resetPage) response.data else uiState.users + response.data,
                            isLoading = false,
                            hasMore = response.data.size >= uiState.pageSize
                        )
                        if (resetPage) {
                            _effects.send(UserManagementEffect.ScrollToTop)
                        }
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = response.msg
                        )
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    val errorMessage = "加载用户失败: ${error.message}"
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                    _effects.send(UserManagementEffect.ShowError(errorMessage))
                }
        }
    }

    private fun loadMoreUsers() {
        if (uiState.isLoading || !uiState.hasMore) return

        uiState = uiState.copy(currentPage = uiState.currentPage + 1)
        loadUsers(resetPage = false)
    }

    private fun refreshUsers() {
        uiState = uiState.copy(isRefreshing = true, currentPage = 1)

        viewModelScope.launch {
            val params = UserQueryParams(
                page = 1,
                limit = uiState.pageSize,
                userName = if (uiState.searchQuery.isNotBlank()) uiState.searchQuery else null,
                isAdmin = uiState.filterAdmin,
                isDeleted = uiState.filterDeleted,
                isBanned = uiState.filterBanned
            )

            userRepository.getUsers(params)
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            users = response.data,
                            isRefreshing = false,
                            hasMore = response.data.size >= uiState.pageSize
                        )
                        _effects.send(UserManagementEffect.ShowMessage("刷新成功"))
                    } else {
                        uiState = uiState.copy(
                            isRefreshing = false,
                            errorMessage = response.msg
                        )
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    uiState = uiState.copy(isRefreshing = false)
                    _effects.send(UserManagementEffect.ShowError("刷新失败: ${error.message}"))
                }
        }
    }

    private fun applyFilters() {
        uiState = uiState.copy(isFilterExpanded = false)
        loadUsers(resetPage = true)
    }

    private fun clearFilters() {
        uiState = uiState.copy(
            searchQuery = "",
            filterAdmin = null,
            filterDeleted = null,
            filterBanned = null,
            isFilterExpanded = false
        )
        loadUsers(resetPage = true)
    }

    private fun banUser(user: com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo) {
        viewModelScope.launch {
            userRepository.banUser(user)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(UserManagementEffect.ShowMessage("用户已封禁"))
                        refreshUsers()
                    } else {
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("封禁失败: ${error.message}"))
                }
        }
    }

    private fun unbanUser(user: com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo) {
        viewModelScope.launch {
            userRepository.unbanUser(user)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(UserManagementEffect.ShowMessage("用户已解封"))
                        refreshUsers()
                    } else {
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("解封失败: ${error.message}"))
                }
        }
    }

    private fun deleteUser(user: com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(UserManagementEffect.ShowMessage("用户已删除"))
                        refreshUsers()
                    } else {
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("删除失败: ${error.message}"))
                }
        }
    }

    private fun restoreUser(user: com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo) {
        viewModelScope.launch {
            userRepository.restoreUser(user)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(UserManagementEffect.ShowMessage("用户已恢复"))
                        refreshUsers()
                    } else {
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("恢复失败: ${error.message}"))
                }
        }
    }

    private fun saveUser(user: com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo) {
        viewModelScope.launch {
            val request = userRepository.createUpdateRequest(user)
            userRepository.updateUser(request)
                .onSuccess { response ->
                    if (response.code == "200") {
                        _effects.send(UserManagementEffect.ShowMessage("用户信息已更新"))
                        uiState = uiState.copy(isEditDialogOpen = false, editingUser = null)
                        refreshUsers()
                    } else {
                        _effects.send(UserManagementEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("更新失败: ${error.message}"))
                }
        }
    }
}
