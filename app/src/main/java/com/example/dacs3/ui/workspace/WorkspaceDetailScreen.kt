package com.example.dacs3.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.local.ChannelEntity
import com.example.dacs3.data.local.UserEntity
import com.example.dacs3.data.local.WorkspaceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceDetailScreen(
    workspaceId: String,
    viewModel: WorkspaceDetailViewModel = hiltViewModel(),
    onNavigateToChannel: (String) -> Unit = {},
    onNavigateToEpics: () -> Unit = {},
    onCreateChannel: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val workspace by viewModel.workspace.collectAsState(initial = null)
    val channels by viewModel.channels.collectAsState(initial = emptyList())
    val members by viewModel.members.collectAsState(initial = emptyList())
    val taskCount by viewModel.taskCount.collectAsState()
    
    // Set the workspace ID when the screen is created
    LaunchedEffect(workspaceId) {
        viewModel.setWorkspaceId(workspaceId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                workspace?.let { 
                    Text(it.name) 
                } ?: Text("Loading...")
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6B4EFF),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )
        
        // Main content
        workspace?.let { ws ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Workspace info section
                item {
                    WorkspaceInfoCard(
                        workspace = ws,
                        memberCount = members.size,
                        channelCount = channels.size,
                        taskCount = taskCount
                    )
                }
                
                // Quick actions
                item {
                    QuickActions(
                        onEpicsClick = onNavigateToEpics,
                        onAddChannelClick = onCreateChannel
                    )
                }
                
                // Channels section
                item {
                    Text(
                        text = "Channels",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                
                // Channel list
                if (channels.isNotEmpty()) {
                    items(channels) { channel ->
                        ChannelItem(
                            channel = channel,
                            onClick = { onNavigateToChannel(channel.channelId) }
                        )
                    }
                } else {
                    item {
                        EmptyStateCard(
                            message = "No channels yet",
                            buttonText = "Create Channel",
                            onClick = onCreateChannel
                        )
                    }
                }
                
                // Members section
                item {
                    Text(
                        text = "Members",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                
                // Member list
                if (members.isNotEmpty()) {
                    items(members) { member ->
                        MemberItem(user = member)
                    }
                } else {
                    item {
                        EmptyStateCard(
                            message = "No members yet",
                            buttonText = "Invite Members",
                            onClick = {}
                        )
                    }
                }
                
                // Add some bottom padding
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6B4EFF))
            }
        }
    }
}

@Composable
private fun WorkspaceInfoCard(
    workspace: WorkspaceEntity,
    memberCount: Int,
    channelCount: Int,
    taskCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = workspace.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = workspace.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.Group,
                    label = "Members",
                    value = memberCount.toString()
                )
                
                InfoItem(
                    icon = Icons.Default.Tag,
                    label = "Channels",
                    value = channelCount.toString()
                )
                
                InfoItem(
                    icon = Icons.Default.Task,
                    label = "Tasks",
                    value = taskCount.toString()
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B4EFF),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun QuickActions(
    onEpicsClick: () -> Unit,
    onAddChannelClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Default.Assignment,
            label = "Epics",
            color = Color(0xFF6B4EFF),
            onClick = onEpicsClick
        )
        
        ActionButton(
            icon = Icons.Default.Tag,
            label = "Add Channel",
            color = Color(0xFF4CD964),
            onClick = onAddChannelClick
        )
        
        ActionButton(
            icon = Icons.Default.PersonAdd,
            label = "Invite",
            color = Color(0xFFFFA726),
            onClick = { /* TODO: Implement invite functionality */ }
        )
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun ChannelItem(
    channel: ChannelEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (channel.isPrivate) 
                            Color(0xFFEF5350).copy(alpha = 0.1f) 
                        else 
                            Color(0xFF6B4EFF).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (channel.isPrivate) Icons.Default.Lock else Icons.Default.Tag,
                    contentDescription = null,
                    tint = if (channel.isPrivate) Color(0xFFEF5350) else Color(0xFF6B4EFF),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = channel.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            
            if (channel.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6B4EFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = channel.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberItem(user: UserEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9E8CFF),
                                Color(0xFF7B6AF9)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (user.isOnline) Color(0xFF4CD964) else Color.Gray)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (user.isOnline) "Online" else "Offline",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    message: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDE9FF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(text = buttonText)
            }
        }
    }
} 