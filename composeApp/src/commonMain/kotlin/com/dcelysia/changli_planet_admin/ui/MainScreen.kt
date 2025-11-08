package com.dcelysia.changli_planet_admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dcelysia.changli_planet_admin.core.storage.TokenManager
import com.dcelysia.changli_planet_admin.feature.post.presentation.ui.PostManagementScreen
import com.dcelysia.changli_planet_admin.feature.user.presentation.ui.UserManagementScreen
import com.dcelysia.changli_planet_admin.navigation.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val username = TokenManager.getUsername() ?: "管理员"
    
    // 当前选中的页面
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    
    BoxWithConstraints {
        val screenWidth = maxWidth
        // 判断是否使用移动端布局（宽度小于600dp）
        val isCompactScreen = screenWidth < 600.dp
        
        if (isCompactScreen) {
            // 移动端布局：底部导航栏
            MobileLayout(
                currentScreen = currentScreen,
                onScreenChange = { screen: Screen -> currentScreen = screen },
                username = username,
                onLogout = onLogout
            )
        } else {
            // 桌面端布局：侧边导航栏
            DesktopLayout(
                currentScreen = currentScreen,
                onScreenChange = { screen: Screen -> currentScreen = screen },
                username = username,
                onLogout = onLogout
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileLayout(
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit,
    username: String,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = currentScreen.title,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "退出登录")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen.route == screen.route,
                        onClick = { onScreenChange(screen) },
                        icon = { 
                            Icon(screen.icon, contentDescription = screen.title)
                        },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScreenContent(currentScreen)
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopLayout(
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit,
    username: String,
    onLogout: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // 侧边导航栏
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surface,
            header = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "管理后台",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "管理后台",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            drawerItems.forEach { screen ->
                NavigationRailItem(
                    selected = currentScreen.route == screen.route,
                    onClick = { onScreenChange(screen) },
                    icon = { 
                        Icon(screen.icon, contentDescription = screen.title)
                    },
                    label = { Text(screen.title) }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 用户信息和退出按钮
            NavigationRailItem(
                selected = false,
                onClick = onLogout,
                icon = { 
                    Icon(Icons.Default.Logout, contentDescription = "退出登录")
                },
                label = { Text("退出") }
            )
        }
        
        // 主内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column {
                // 顶部标题栏
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentScreen.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "欢迎，$username",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // 页面内容
                ScreenContent(currentScreen)
            }
        }
    }
}

@Composable
fun ScreenContent(screen: Screen) {
    when (screen) {
        Screen.Dashboard -> DashboardScreen()
        Screen.UserManagement -> UserManagementScreen()
        Screen.PostManagement -> PostManagementScreen()
        Screen.Settings -> SettingsScreen()
        Screen.Profile -> ProfileScreen()
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "设置",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "设置页面",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "功能开发中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "个人资料",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "个人资料",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "功能开发中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
