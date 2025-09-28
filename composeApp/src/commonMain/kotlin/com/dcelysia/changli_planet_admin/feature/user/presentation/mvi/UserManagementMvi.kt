package com.dcelysia.changli_planet_admin.feature.user.presentation.mvi

import com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo

// MVI State
data class UserManagementUiState(
    val users: List<UserFullInfo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

// MVI Intent
sealed class UserManagementIntent {
    object LoadUsers : UserManagementIntent()
    object RefreshUsers : UserManagementIntent()
    data class BanUser(val userId: Int) : UserManagementIntent()
    data class UnbanUser(val userId: Int) : UserManagementIntent()
    data class DeleteUser(val userId: Int) : UserManagementIntent()
    object ClearError : UserManagementIntent()
}

// MVI Effect
sealed class UserManagementEffect {
    data class ShowMessage(val message: String) : UserManagementEffect()
    data class ShowError(val message: String) : UserManagementEffect()
}
