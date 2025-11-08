package com.dcelysia.changli_planet_admin.feature.user.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo
import com.dcelysia.changli_planet_admin.feature.user.presentation.mvi.UserManagementIntent
import com.dcelysia.changli_planet_admin.feature.user.presentation.mvi.UserManagementEffect
import com.dcelysia.changli_planet_admin.feature.user.presentation.viewmodel.UserManagementViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun UserManagementScreen(
    viewModel: UserManagementViewModel = remember { UserManagementViewModel() }
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 添加性能监控
    LaunchedEffect(Unit) {
        println("UserManagementScreen - Component initialized")
    }

    // Handle effects
    LaunchedEffect(viewModel) {
        println("UserManagementScreen - Effects collection started")
        viewModel.effects.collect { effect ->
            println("UserManagementScreen - Effect received: $effect")
            when (effect) {
                is UserManagementEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is UserManagementEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is UserManagementEffect.ScrollToTop -> {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // 标题和操作栏
            UserManagementHeader(
                onRefresh = { viewModel.handleIntent(UserManagementIntent.RefreshUsers) },
                isRefreshing = uiState.isRefreshing
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 搜索和过滤栏
            SearchAndFilterBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.handleIntent(UserManagementIntent.UpdateSearchQuery(it)) },
                onSearch = { viewModel.handleIntent(UserManagementIntent.ApplyFilters) },
                isFilterExpanded = uiState.isFilterExpanded,
                onToggleFilter = { viewModel.handleIntent(UserManagementIntent.ToggleFilterExpanded) },
                filterAdmin = uiState.filterAdmin,
                filterDeleted = uiState.filterDeleted,
                filterBanned = uiState.filterBanned,
                onAdminFilterChange = { viewModel.handleIntent(UserManagementIntent.UpdateAdminFilter(it)) },
                onDeletedFilterChange = { viewModel.handleIntent(UserManagementIntent.UpdateDeletedFilter(it)) },
                onBannedFilterChange = { viewModel.handleIntent(UserManagementIntent.UpdateBannedFilter(it)) },
                onApplyFilters = { viewModel.handleIntent(UserManagementIntent.ApplyFilters) },
                onClearFilters = { viewModel.handleIntent(UserManagementIntent.ClearFilters) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 用户列表
            when {
                uiState.isLoading && uiState.users.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("加载中...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                uiState.users.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    UserList(
                        users = uiState.users,
                        listState = listState,
                        isLoading = uiState.isLoading,
                        hasMore = uiState.hasMore,
                        onLoadMore = { viewModel.handleIntent(UserManagementIntent.LoadMoreUsers) },
                        onBanClick = { user ->
                            if (user.userResp.isBanned) {
                                viewModel.handleIntent(UserManagementIntent.UnbanUser(user))
                            } else {
                                viewModel.handleIntent(UserManagementIntent.BanUser(user))
                            }
                        },
                        onDeleteClick = { user ->
                            if (user.userResp.isDeleted) {
                                viewModel.handleIntent(UserManagementIntent.RestoreUser(user))
                            } else {
                                viewModel.handleIntent(UserManagementIntent.DeleteUser(user))
                            }
                        },
                        onEditClick = { user ->
                            viewModel.handleIntent(UserManagementIntent.OpenEditDialog(user))
                        }
                    )
                }
            }
        }
    }

    // 编辑对话框
    if (uiState.isEditDialogOpen && uiState.editingUser != null) {
        UserEditDialog(
            user = uiState.editingUser,
            onDismiss = { viewModel.handleIntent(UserManagementIntent.CloseEditDialog) },
            onSave = { updatedUser ->
                viewModel.handleIntent(UserManagementIntent.SaveUser(updatedUser))
            }
        )
    }
}

@Composable
fun UserManagementHeader(
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "用户管理",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "管理系统用户信息",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !isRefreshing
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Refresh, contentDescription = "刷新")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isFilterExpanded: Boolean,
    onToggleFilter: () -> Unit,
    filterAdmin: Boolean?,
    filterDeleted: Boolean?,
    filterBanned: Boolean?,
    onAdminFilterChange: (Boolean?) -> Unit,
    onDeletedFilterChange: (Boolean?) -> Unit,
    onBannedFilterChange: (Boolean?) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit
) {
    Column {
        // 搜索栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("搜索用户名...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                shape = RoundedCornerShape(12.dp)
            )

            FilledTonalButton(
                onClick = onToggleFilter,
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    if (isFilterExpanded) Icons.Default.FilterAltOff else Icons.Default.FilterAlt,
                    contentDescription = "过滤"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("过滤")
            }
        }

        // 过滤选项
        AnimatedVisibility(
            visible = isFilterExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "过滤条件",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // 管理员过滤
                    FilterChipGroup(
                        label = "管理员",
                        options = listOf(null to "全部", true to "是", false to "否"),
                        selectedValue = filterAdmin,
                        onValueChange = onAdminFilterChange
                    )

                    // 删除状态过滤
                    FilterChipGroup(
                        label = "删除状态",
                        options = listOf(null to "全部", true to "已删除", false to "正常"),
                        selectedValue = filterDeleted,
                        onValueChange = onDeletedFilterChange
                    )

                    // 封禁状态过滤
                    FilterChipGroup(
                        label = "封禁状态",
                        options = listOf(null to "全部", true to "已封禁", false to "正常"),
                        selectedValue = filterBanned,
                        onValueChange = onBannedFilterChange
                    )

                    // 操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClearFilters,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("清除")
                        }
                        Button(
                            onClick = onApplyFilters,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("应用")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterChipGroup(
    label: String,
    options: List<Pair<T, String>>,
    selectedValue: T,
    onValueChange: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (value, text) ->
                FilterChip(
                    selected = selectedValue == value,
                    onClick = { onValueChange(value) },
                    label = { Text(text) },
                    leadingIcon = if (selectedValue == value) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
fun UserList(
    users: List<UserFullInfo>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    isLoading: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onBanClick: (UserFullInfo) -> Unit,
    onDeleteClick: (UserFullInfo) -> Unit,
    onEditClick: (UserFullInfo) -> Unit
) {
    // 检测滚动到底部
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 3 && hasMore && !isLoading
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(users, key = { it.userResp.userId }) { user ->
            UserCard(
                user = user,
                onBanClick = { onBanClick(user) },
                onDeleteClick = { onDeleteClick(user) },
                onEditClick = { onEditClick(user) }
            )
        }

        // 加载更多指示器
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }

        // 没有更多数据
        if (!hasMore && users.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "没有更多数据了",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: UserFullInfo,
    onBanClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 用户基本信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // 头像
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.userResp.username.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // 用户名和标签
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = user.userResp.username,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // 标签
                            if (user.userResp.isAdmin > 0) {
                                StatusBadge("管理员", MaterialTheme.colorScheme.primary)
                            }
                            if (user.userResp.isBanned) {
                                StatusBadge("已封禁", MaterialTheme.colorScheme.error)
                            }
                            if (user.userResp.isDeleted) {
                                StatusBadge("已删除", MaterialTheme.colorScheme.outline)
                            }
                        }

                        Text(
                            text = "学号: ${user.userStatsResp.studentNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // 操作按钮
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onBanClick) {
                        Icon(
                            if (user.userResp.isBanned) Icons.Default.Person else Icons.Default.Block,
                            contentDescription = if (user.userResp.isBanned) "解封" else "封禁",
                            tint = if (user.userResp.isBanned)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            if (user.userResp.isDeleted) Icons.Default.RestoreFromTrash else Icons.Default.Delete,
                            contentDescription = if (user.userResp.isDeleted) "恢复" else "删除",
                            tint = if (user.userResp.isDeleted)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // 用户统计信息
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("文章", user.userStatsResp.articleCount.toString(), Icons.Default.Article)
                StatItem("评论", user.userStatsResp.commentCount.toString(), Icons.Default.Comment)
                StatItem("获赞", user.userStatsResp.likedCount.toString(), Icons.Default.ThumbUp)
                StatItem("等级", "Lv.${user.userProfileResp.userLevel}", Icons.Default.Grade)
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.1f),
        contentColor = color
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.People,
                contentDescription = "无用户",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Text(
                text = "暂无用户数据",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "尝试调整搜索或过滤条件",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}