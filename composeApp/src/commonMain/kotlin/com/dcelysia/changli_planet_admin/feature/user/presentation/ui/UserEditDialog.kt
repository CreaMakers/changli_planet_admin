package com.dcelysia.changli_planet_admin.feature.user.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dcelysia.changli_planet_admin.feature.user.data.model.UserFullInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditDialog(
    user: UserFullInfo,
    onDismiss: () -> Unit,
    onSave: (UserFullInfo) -> Unit
) {
    var editedUser by remember { mutableStateOf(user) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 标题栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "编辑用户",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 表单内容
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 基本信息
                    Text(
                        text = "基本信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = editedUser.userResp.username,
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userResp = editedUser.userResp.copy(username = it)
                            )
                        },
                        label = { Text("用户名") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = editedUser.userResp.description ?: "",
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userResp = editedUser.userResp.copy(description = it)
                            )
                        },
                        label = { Text("用户描述") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    
                    // 权限设置
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilterChip(
                            selected = editedUser.userResp.isAdmin > 0,
                            onClick = {
                                editedUser = editedUser.copy(
                                    userResp = editedUser.userResp.copy(
                                        isAdmin = if (editedUser.userResp.isAdmin > 0) 0 else 1
                                    )
                                )
                            },
                            label = { Text("管理员") },
                            leadingIcon = {
                                Icon(
                                    if (editedUser.userResp.isAdmin > 0) Icons.Default.CheckCircle else Icons.Default.Circle,
                                    contentDescription = null
                                )
                            }
                        )
                        
                        FilterChip(
                            selected = editedUser.userResp.isBanned,
                            onClick = {
                                editedUser = editedUser.copy(
                                    userResp = editedUser.userResp.copy(
                                        isBanned = !editedUser.userResp.isBanned
                                    )
                                )
                            },
                            label = { Text("已封禁") },
                            leadingIcon = {
                                Icon(
                                    if (editedUser.userResp.isBanned) Icons.Default.CheckCircle else Icons.Default.Circle,
                                    contentDescription = null
                                )
                            }
                        )
                        
                        FilterChip(
                            selected = editedUser.userResp.isDeleted,
                            onClick = {
                                editedUser = editedUser.copy(
                                    userResp = editedUser.userResp.copy(
                                        isDeleted = !editedUser.userResp.isDeleted
                                    )
                                )
                            },
                            label = { Text("已删除") },
                            leadingIcon = {
                                Icon(
                                    if (editedUser.userResp.isDeleted) Icons.Default.CheckCircle else Icons.Default.Circle,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    
                    Divider()
                    
                    // 个人资料
                    Text(
                        text = "个人资料",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = editedUser.userProfileResp.bio ?: "",
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userProfileResp = editedUser.userProfileResp.copy(bio = it)
                            )
                        },
                        label = { Text("个人简介") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    
                    OutlinedTextField(
                        value = editedUser.userProfileResp.grade ?: "未设置",
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userProfileResp = editedUser.userProfileResp.copy(grade = it)
                            )
                        },
                        label = { Text("年级") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editedUser.userProfileResp.userLevel.toString(),
                            onValueChange = { 
                                editedUser = editedUser.copy(
                                    userProfileResp = editedUser.userProfileResp.copy(
                                        userLevel = it.toIntOrNull() ?: editedUser.userProfileResp.userLevel
                                    )
                                )
                            },
                            label = { Text("等级") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = editedUser.userProfileResp.gender.toString(),
                            onValueChange = { 
                                editedUser = editedUser.copy(
                                    userProfileResp = editedUser.userProfileResp.copy(
                                        gender = it.toIntOrNull() ?: editedUser.userProfileResp.gender
                                    )
                                )
                            },
                            label = { Text("性别 (0:女 1:男)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    
                    OutlinedTextField(
                        value = editedUser.userProfileResp.location ?: "",
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userProfileResp = editedUser.userProfileResp.copy(location = it)
                            )
                        },
                        label = { Text("所在地") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = editedUser.userProfileResp.website ?: "",
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userProfileResp = editedUser.userProfileResp.copy(website = it)
                            )
                        },
                        label = { Text("个人网站") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Divider()
                    
                    // 统计信息
                    Text(
                        text = "统计信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = editedUser.userStatsResp.studentNumber ?: "无绑定学号",
                        onValueChange = { 
                            editedUser = editedUser.copy(
                                userStatsResp = editedUser.userStatsResp.copy(studentNumber = it)
                            )
                        },
                        label = { Text("学号") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editedUser.userStatsResp.coinCount.toString(),
                            onValueChange = { 
                                editedUser = editedUser.copy(
                                    userStatsResp = editedUser.userStatsResp.copy(
                                        coinCount = it.toIntOrNull() ?: editedUser.userStatsResp.coinCount
                                    )
                                )
                            },
                            label = { Text("金币") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = editedUser.userStatsResp.xp.toString(),
                            onValueChange = { 
                                editedUser = editedUser.copy(
                                    userStatsResp = editedUser.userStatsResp.copy(
                                        xp = it.toIntOrNull() ?: editedUser.userStatsResp.xp
                                    )
                                )
                            },
                            label = { Text("经验值") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = { onSave(editedUser) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("保存")
                    }
                }
            }
        }
    }
}
