package com.dcelysia.changli_planet_admin.feature.auth.presentation.mvi

// MVI State
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

// MVI Intent
sealed class LoginIntent {
    data class UpdateUsername(val username: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    object Login : LoginIntent()
    object ClearError : LoginIntent()
    object ResetState : LoginIntent()
}

// MVI Effect (one-time events)
sealed class LoginEffect {
    object NavigateToHome : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
}
