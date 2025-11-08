package com.dcelysia.changli_planet_admin.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dcelysia.changli_planet_admin.feature.auth.presentation.mvi.LoginIntent
import com.dcelysia.changli_planet_admin.feature.auth.presentation.mvi.LoginEffect
import com.dcelysia.changli_planet_admin.feature.auth.presentation.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel? = null
) {
    val loginViewModel = viewModel ?: remember { LoginViewModel() }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState = loginViewModel.uiState
    
    // Handle effects
    LaunchedEffect(loginViewModel) {
        loginViewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> {
                    onLoginSuccess()
                }
                is LoginEffect.ShowError -> {
                    // Error is already shown in UI state
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),  // 蓝色
                        Color(0xFF0288D1)   // 浅蓝色
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo 和标题区域
            Card(
                modifier = Modifier
                    .size(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌍",
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "长理星球管理",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "欢迎回来！请登录您的账户",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 登录表单卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 账号输入框
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = { loginViewModel.handleIntent(LoginIntent.UpdateUsername(it)) },
                        label = { Text("账号") },
                        placeholder = { Text("请输入您的账号") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "账号",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 密码输入框
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { loginViewModel.handleIntent(LoginIntent.UpdatePassword(it)) },
                        label = { Text("密码") },
                        placeholder = { Text("请输入您的密码") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "密码",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 错误信息显示
                    uiState.errorMessage?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 登录按钮
                    Button(
                        onClick = { loginViewModel.handleIntent(LoginIntent.Login) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = uiState.username.isNotBlank() && uiState.password.isNotBlank() && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "登录",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 忘记密码
                    TextButton(
                        onClick = {
                            // TODO: 实现忘记密码逻辑
                        }
                    ) {
                        Text(
                            text = "忘记密码？",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 版权信息
            Text(
                text = "© 2024 长理星球. 保留所有权利",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
