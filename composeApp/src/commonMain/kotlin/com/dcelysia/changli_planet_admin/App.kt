package com.dcelysia.changli_planet_admin

import androidx.compose.runtime.*
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.dcelysia.changli_planet_admin.feature.auth.presentation.ui.LoginScreen
import com.dcelysia.changli_planet_admin.ui.MainScreen

@Composable
@Preview
fun App() {
    var isLoggedIn by remember { mutableStateOf(TokenManager.isLoggedIn()) }
    
    if (isLoggedIn) {
        MainScreen(
            onLogout = {
                TokenManager.clearAll()
                isLoggedIn = false
            }
        )
    } else {
        LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
    }
}