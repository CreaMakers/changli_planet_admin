package com.dcelysia.changli_planet_admin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    object Dashboard : Screen("dashboard", "仪表盘", Icons.Default.Dashboard, "系统概览")
    object UserManagement : Screen("users", "用户管理", Icons.Default.People, "管理系统用户")
    object PostManagement : Screen("posts", "帖子管理", Icons.Default.Article, "管理用户帖子")
    object Settings : Screen("settings", "设置", Icons.Default.Settings, "系统设置")
    object Profile : Screen("profile", "个人资料", Icons.Default.Person, "管理个人信息")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.UserManagement,
    Screen.PostManagement,
    Screen.Settings
)

val drawerItems = listOf(
    Screen.Dashboard,
    Screen.UserManagement, 
    Screen.PostManagement,
    Screen.Profile,
    Screen.Settings
)
