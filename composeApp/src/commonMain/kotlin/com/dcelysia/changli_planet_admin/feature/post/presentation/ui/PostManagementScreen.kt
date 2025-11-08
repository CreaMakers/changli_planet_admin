package com.dcelysia.changli_planet_admin.feature.post.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dcelysia.changli_planet_admin.feature.post.data.model.FreshNews
import com.dcelysia.changli_planet_admin.feature.post.presentation.mvi.PostManagementIntent
import com.dcelysia.changli_planet_admin.feature.post.presentation.mvi.PostManagementEffect
import com.dcelysia.changli_planet_admin.feature.post.presentation.viewmodel.PostManagementViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PostManagementScreen(
    viewModel: PostManagementViewModel = remember { PostManagementViewModel() }
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PostManagementEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is PostManagementEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is PostManagementEffect.ScrollToTop -> {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 顶部工具栏
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 显示帖子统计信息
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Article,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "共 ${uiState.posts.size} 篇帖子",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    // 刷新按钮
                    FilledTonalIconButton(
                        onClick = { viewModel.handleIntent(PostManagementIntent.RefreshPosts) },
                        enabled = !uiState.isRefreshing
                    ) {
                        if (uiState.isRefreshing) {
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

            Spacer(modifier = Modifier.height(12.dp))

            // 标签栏
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.handleIntent(PostManagementIntent.SelectTab(0)) },
                        text = { Text("待审核") },
                        icon = { Icon(Icons.Default.Pending, contentDescription = null) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.handleIntent(PostManagementIntent.SelectTab(1)) },
                        text = { Text("已通过") },
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 2,
                        onClick = { viewModel.handleIntent(PostManagementIntent.SelectTab(2)) },
                        text = { Text("已拒绝") },
                        icon = { Icon(Icons.Default.Cancel, contentDescription = null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 帖子列表
            Box(modifier = Modifier.padding(horizontal = 16.dp).weight(1f)) {
                when {
                    uiState.isLoading && uiState.posts.isEmpty() -> {
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
                    uiState.posts.isEmpty() -> {
                        EmptyPostState()
                    }
                    else -> {
                        PostList(
                            posts = uiState.posts,
                            listState = listState,
                            isLoading = uiState.isLoading,
                            hasMore = uiState.hasMore,
                            selectedTab = uiState.selectedTab,
                            onLoadMore = { viewModel.handleIntent(PostManagementIntent.LoadMorePosts) },
                            onApprove = { post -> viewModel.handleIntent(PostManagementIntent.ApprovePost(post)) },
                            onReject = { post -> viewModel.handleIntent(PostManagementIntent.RejectPost(post)) },
                            onPreview = { post -> viewModel.handleIntent(PostManagementIntent.OpenPreview(post)) }
                        )
                    }
                }
            }
        }
    }

    // 预览对话框
    if (uiState.isPreviewDialogOpen && uiState.previewPost != null) {
        PostPreviewDialog(
            post = uiState.previewPost,
            onDismiss = { viewModel.handleIntent(PostManagementIntent.ClosePreview) },
            onApprove = { 
                viewModel.handleIntent(PostManagementIntent.ApprovePost(uiState.previewPost))
                viewModel.handleIntent(PostManagementIntent.ClosePreview)
            },
            onReject = { 
                viewModel.handleIntent(PostManagementIntent.RejectPost(uiState.previewPost))
                viewModel.handleIntent(PostManagementIntent.ClosePreview)
            },
            showActions = uiState.selectedTab == 0
        )
    }
}

@Composable
fun PostList(
    posts: List<FreshNews>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    isLoading: Boolean,
    hasMore: Boolean,
    selectedTab: Int,
    onLoadMore: () -> Unit,
    onApprove: (FreshNews) -> Unit,
    onReject: (FreshNews) -> Unit,
    onPreview: (FreshNews) -> Unit
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
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(posts, key = { it.freshNewsCheckId }) { post ->
            PostCard(
                post = post,
                showActions = selectedTab == 0,  // 只有待审核标签显示操作按钮
                onApprove = { onApprove(post) },
                onReject = { onReject(post) },
                onPreview = { onPreview(post) }
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
        if (!hasMore && posts.isNotEmpty()) {
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
fun PostCard(
    post: FreshNews,
    showActions: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPreview,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 标题和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                StatusBadge(post)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 内容预览
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // 图片预览
            val imageUrls = post.getImageUrls()
            if (imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imageUrls.take(3)) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "帖子图片",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (imageUrls.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${imageUrls.size - 3}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // 时间和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.createTime.substringBefore('T'),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                if (showActions) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = onReject,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("拒绝")
                        }
                        
                        Button(onClick = onApprove) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("通过")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(post: FreshNews) {
    val (color, text) = when (post.checkStatus) {
        0 -> MaterialTheme.colorScheme.tertiary to "待审核"
        1 -> MaterialTheme.colorScheme.primary to "已通过"
        2 -> MaterialTheme.colorScheme.error to "已拒绝"
        else -> MaterialTheme.colorScheme.outline to "未知"
    }
    
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.1f),
        contentColor = color
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyPostState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Article,
                contentDescription = "无帖子",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Text(
                text = "暂无帖子数据",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
