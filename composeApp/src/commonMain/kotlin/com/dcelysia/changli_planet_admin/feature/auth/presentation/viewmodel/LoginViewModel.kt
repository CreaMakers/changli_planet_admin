package com.dcelysia.changli_planet_admin.feature.auth.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dcelysia.changli_planet_admin.feature.auth.data.repository.AuthRepository
import com.dcelysia.changli_planet_admin.feature.auth.presentation.mvi.LoginIntent
import com.dcelysia.changli_planet_admin.feature.auth.presentation.mvi.LoginUiState
import com.dcelysia.changli_planet_admin.feature.auth.presentation.mvi.LoginEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    
    var uiState by mutableStateOf(LoginUiState())
        private set
        
    private val _effects = Channel<LoginEffect>()
    val effects = _effects.receiveAsFlow()
    
    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateUsername -> {
                uiState = uiState.copy(username = intent.username, errorMessage = null)
            }
            is LoginIntent.UpdatePassword -> {
                uiState = uiState.copy(password = intent.password, errorMessage = null)
            }
            is LoginIntent.Login -> {
                login()
            }
            is LoginIntent.ClearError -> {
                uiState = uiState.copy(errorMessage = null)
            }
            is LoginIntent.ResetState -> {
                uiState = LoginUiState()
            }
        }
    }
    
    private fun login() {
        if (uiState.username.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "用户名和密码不能为空")
            return
        }
        
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            authRepository.login(uiState.username, uiState.password)
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                        _effects.send(LoginEffect.NavigateToHome)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = response.msg
                        )
                        _effects.send(LoginEffect.ShowError(response.msg))
                    }
                }
                .onFailure { error ->
                    val errorMessage = "登录失败: ${error.message ?: "网络错误"}"
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                    _effects.send(LoginEffect.ShowError(errorMessage))
                }
        }
    }
}
