package com.dcelysia.changli_planet_admin.feature.user.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        handleIntent(UserManagementIntent.LoadUsers)
    }
    
    fun handleIntent(intent: UserManagementIntent) {
        when (intent) {
            is UserManagementIntent.LoadUsers -> {
                loadUsers()
            }
            is UserManagementIntent.RefreshUsers -> {
                refreshUsers()
            }
            is UserManagementIntent.BanUser -> {
                banUser(intent.userId)
            }
            is UserManagementIntent.UnbanUser -> {
                unbanUser(intent.userId)
            }
            is UserManagementIntent.DeleteUser -> {
                deleteUser(intent.userId)
            }
            is UserManagementIntent.ClearError -> {
                uiState = uiState.copy(errorMessage = null)
            }
        }
    }
    
    private fun loadUsers() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            userRepository.getUsers()
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            users = response.data,
                            isLoading = false
                        )
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
    
    private fun refreshUsers() {
        uiState = uiState.copy(isRefreshing = true)
        
        viewModelScope.launch {
            userRepository.getUsers()
                .onSuccess { response ->
                    if (response.code == "200") {
                        uiState = uiState.copy(
                            users = response.data,
                            isRefreshing = false
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
    
    private fun banUser(userId: Int) {
        viewModelScope.launch {
            userRepository.banUser(userId)
                .onSuccess {
                    _effects.send(UserManagementEffect.ShowMessage("用户已封禁"))
                    loadUsers() // 重新加载用户列表
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("封禁失败: ${error.message}"))
                }
        }
    }
    
    private fun unbanUser(userId: Int) {
        viewModelScope.launch {
            userRepository.unbanUser(userId)
                .onSuccess {
                    _effects.send(UserManagementEffect.ShowMessage("用户已解封"))
                    loadUsers() // 重新加载用户列表
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("解封失败: ${error.message}"))
                }
        }
    }
    
    private fun deleteUser(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
                .onSuccess {
                    _effects.send(UserManagementEffect.ShowMessage("用户已删除"))
                    loadUsers() // 重新加载用户列表
                }
                .onFailure { error ->
                    _effects.send(UserManagementEffect.ShowError("删除失败: ${error.message}"))
                }
        }
    }
}
