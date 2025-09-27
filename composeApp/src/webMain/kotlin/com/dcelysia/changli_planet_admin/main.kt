package com.dcelysia.changli_planet_admin

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        MaterialTheme(typography = HTypography()) {
            App()
        }
    }
}