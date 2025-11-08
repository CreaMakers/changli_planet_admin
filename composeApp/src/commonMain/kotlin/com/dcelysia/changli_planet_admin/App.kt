package com.dcelysia.changli_planet_admin

import androidx.compose.runtime.*
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.dcelysia.changli_planet_admin.feature.auth.presentation.ui.LoginScreen
import com.dcelysia.changli_planet_admin.ui.MainScreen
import com.dcelysia.changli_planet_admin.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    var isLoggedIn by remember { mutableStateOf(TokenManager.isLoggedIn()) }
    AppTheme {
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
}