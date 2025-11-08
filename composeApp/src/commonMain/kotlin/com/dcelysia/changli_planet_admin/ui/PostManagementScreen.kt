package com.dcelysia.changli_planet_admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 临时帖子数据模型
data class PostItem(
    val id: Int,
    val title: String,
    val content: String,
    val author: String,
    val createTime: String,
    val status: PostStatus,
    val likeCount: Int,
    val commentCount: Int
)

enum class PostStatus {
    PUBLISHED, PENDING, REJECTED
}

@Composable
fun PostManagementScreen() {
    // 临时示例数据
    val samplePosts = listOf(
        PostItem(1, "欢迎来到长理星球", "这是一个学习交流的平台，大家可以在这里分享知识...", "creamaker", "2024-12-27 10:30", PostStatus.PUBLISHED, 25, 8),
        PostItem(2, "如何学好编程？", "编程学习需要持续的练习和思考，今天分享一些个人经验...", "student123", "2024-12-27 09:15", PostStatus.PENDING, 0, 0),
        PostItem(3, "期末考试复习指南", "临近期末考试，为大家整理了一些复习要点和技巧...", "user_test2", "2024-12-26 14:22", PostStatus.PUBLISHED, 42, 15),
        PostItem(4, "违规内容示例", "这是一个包含不当内容的帖子...", "baduser", "2024-12-26 12:00", PostStatus.REJECTED, 0, 0),
    )
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("全部帖子", "待审核", "已拒绝")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "帖子管理",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { /* TODO: 刷新数据 */ }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "刷新")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 选项卡
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 帖子列表
        val filteredPosts = when (selectedTab) {
            1 -> samplePosts.filter { it.status == PostStatus.PENDING }
            2 -> samplePosts.filter { it.status == PostStatus.REJECTED }
            else -> samplePosts
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(filteredPosts) { post ->
                PostCard(
                    post = post,
                    onApprove = { /* TODO: 审核通过 */ },
                    onReject = { /* TODO: 审核拒绝 */ },
                    onDelete = { /* TODO: 删除帖子 */ }
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: PostItem,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 帖子标题和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 状态标签
                Badge(
                    containerColor = when (post.status) {
                        PostStatus.PUBLISHED -> MaterialTheme.colorScheme.primary
                        PostStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        PostStatus.REJECTED -> MaterialTheme.colorScheme.error
                    }
                ) {
                    Text(
                        text = when (post.status) {
                            PostStatus.PUBLISHED -> "已发布"
                            PostStatus.PENDING -> "待审核"
                            PostStatus.REJECTED -> "已拒绝"
                        },
                        fontSize = 10.sp,
                        color = when (post.status) {
                            PostStatus.PUBLISHED -> MaterialTheme.colorScheme.onPrimary
                            PostStatus.PENDING -> MaterialTheme.colorScheme.onSecondary
                            PostStatus.REJECTED -> MaterialTheme.colorScheme.onError
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 帖子内容预览
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 帖子信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "作者: ${post.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "发布时间: ${post.createTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                if (post.status == PostStatus.PUBLISHED) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = "点赞",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${post.likeCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "评论",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${post.commentCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            // 操作按钮
            if (post.status != PostStatus.PUBLISHED) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (post.status == PostStatus.PENDING) {
                        OutlinedButton(
                            onClick = onApprove,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("通过")
                        }
                        
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("拒绝")
                        }
                    }
                    
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
