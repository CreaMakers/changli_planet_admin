package com.dcelysia.changli_planet_admin.feature.user.presentation.mvi

import com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo
import com.dcelysia.changli_planet_admin.feature.user.data.model.UserQueryParams

// MVI State
data class UserManagementUiState(
    val users: List<UserFullInfo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,

    // 分页相关
    val currentPage: Int = 1,
    val pageSize: Int = 10,
    val hasMore: Boolean = true,

    // 搜索和过滤
    val searchQuery: String = "",
    val filterAdmin: Boolean? = null,
    val filterDeleted: Boolean? = null,
    val filterBanned: Boolean? = null,
    val isFilterExpanded: Boolean = false,

    // 编辑相关
    val editingUser: UserFullInfo? = null,
    val isEditDialogOpen: Boolean = false
)

// MVI Intent
sealed class UserManagementIntent {
    object LoadUsers : UserManagementIntent()
    object RefreshUsers : UserManagementIntent()
    object LoadMoreUsers : UserManagementIntent()

    // 搜索和过滤
    data class UpdateSearchQuery(val query: String) : UserManagementIntent()
    data class UpdateAdminFilter(val isAdmin: Boolean?) : UserManagementIntent()
    data class UpdateDeletedFilter(val isDeleted: Boolean?) : UserManagementIntent()
    data class UpdateBannedFilter(val isBanned: Boolean?) : UserManagementIntent()
    object ToggleFilterExpanded : UserManagementIntent()
    object ApplyFilters : UserManagementIntent()
    object ClearFilters : UserManagementIntent()

    // 用户操作
    data class BanUser(val user: UserFullInfo) : UserManagementIntent()
    data class UnbanUser(val user: UserFullInfo) : UserManagementIntent()
    data class DeleteUser(val user: UserFullInfo) : UserManagementIntent()
    data class RestoreUser(val user: UserFullInfo) : UserManagementIntent()
    data class OpenEditDialog(val user: UserFullInfo) : UserManagementIntent()
    object CloseEditDialog : UserManagementIntent()
    data class SaveUser(val user: UserFullInfo) : UserManagementIntent()

    object ClearError : UserManagementIntent()
}

// MVI Effect
sealed class UserManagementEffect {
    data class ShowMessage(val message: String) : UserManagementEffect()
    data class ShowError(val message: String) : UserManagementEffect()
    object ScrollToTop : UserManagementEffect()
}
