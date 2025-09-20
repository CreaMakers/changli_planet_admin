package com.dcelysia.changli_planet_admin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Changli_planet_admin",
    ) {
        App()
    }
}