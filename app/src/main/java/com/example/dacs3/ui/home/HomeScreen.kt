package com.example.dacs3.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.dacs3.data.local.TaskEntity
import com.example.dacs3.data.local.UserEntity

@Composable
fun HomeScreen(viewModel: WorkspaceViewModel = hiltViewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()
    val channels by viewModel.channels.collectAsState(initial = emptyList())
    val directMessageContacts by viewModel.directMessageContacts.collectAsState()
    val unreadCount by viewModel.unreadMessageCount.collectAsState()
    val tasks by viewModel.userTasks.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Main content in a white card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                // Header
                WorkspaceHeader(
                    userName = currentUser?.username ?: "Joshitha"
                )
                
                // Task progress card
                tasks.firstOrNull()?.let { task ->
                    TaskProgressCard(task)
                }
                
                // Unreads section with a channel
                UnreadsSection(channels.filter { it.unreadCount > 0 })
                
                // Channels section 
                ChannelsSection(channels)
                
                // Direct Messages section
                DirectMessagesSection(directMessageContacts)
                
                // Spacer to push bottom nav to bottom
                Spacer(modifier = Modifier.weight(1f))
                
                // Bottom Navigation
                BottomNavigationBar()
            }
        }
    }
}

@Composable
private fun WorkspaceHeader(
    userName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9E8CFF),
                            Color(0xFF7B6AF9)
                        )
                    )
                )
                .padding(2.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9E8CFF),
                                Color(0xFF7B6AF9)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        // Username and workspace info
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = "Hello $userName",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Your Workspace",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // Profile/settings icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF6B4EFF))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TaskProgressCard(task: TaskEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B4EFF)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6B4EFF),
                            Color(0xFF8067FF)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Task information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Your task almost",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "done!",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // View Task button
                    Button(
                        onClick = { /* TODO: Handle view task */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "View Task",
                            color = Color(0xFF6B4EFF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Progress circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(4.dp)
                ) {
                    // Track background
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 5.dp,
                        color = Color.White.copy(alpha = 0.2f)
                    )
                    
                    // Actual progress
                    CircularProgressIndicator(
                        progress = { task.progress.toFloat() / 100f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 5.dp,
                        color = Color.White
                    )
                    
                    // Percentage text
                    Text(
                        text = "${task.progress}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun UnreadsSection(
    unreadChannels: List<ChannelEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ExpandableSectionHeader(
            title = "Unreads",
            isExpanded = true
        )
        
        if (unreadChannels.isNotEmpty()) {
            unreadChannels.forEach { channel ->
                ChannelItem(channel)
            }
        } else {
            // Show a placeholder channel to match design
            ChannelItem(
                ChannelEntity(
                    channelId = "placeholder",
                    name = "abc-xyz",
                    description = "Placeholder",
                    workspaceId = "workspace1",
                    createdBy = "user1",
                    isPrivate = false,
                    unreadCount = 0
                )
            )
        }
    }
}

@Composable
private fun ChannelsSection(
    channels: List<ChannelEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ExpandableSectionHeader(
            title = "Channels",
            isExpanded = true
        )
        
        if (channels.isNotEmpty()) {
            channels.take(3).forEach { channel ->
                ChannelItem(channel)
            }
        } else {
            // Show placeholder channels to match design
            repeat(3) { i ->
                ChannelItem(
                    ChannelEntity(
                        channelId = "placeholder$i",
                        name = "abc-xyz",
                        description = "Placeholder",
                        workspaceId = "workspace1",
                        createdBy = "user1",
                        isPrivate = false,
                        unreadCount = 0
                    )
                )
            }
        }
        
        // Add channel button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { /* TODO: Add channel functionality */ }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add channel",
                tint = Color(0xFF6B4EFF),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add channel",
                color = Color(0xFF6B4EFF),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun DirectMessagesSection(
    users: List<UserEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Direct Messages",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        
        // Show Ali Sarraf 4 times to match the design
        repeat(4) {
            val aliSarraf = users.find { it.username == "Ali Sarraf" } ?: UserEntity(
                userId = "user2",
                username = "Ali Sarraf",
                avatarUrl = null,
                isOnline = true
            )
            
            DirectMessageItem(user = aliSarraf)
        }
    }
}

@Composable
private fun ChannelItem(channel: ChannelEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO: Navigate to channel */ }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (channel.unreadCount > 0) Color(0xFF6B4EFF) else Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = channel.name,
            fontSize = 15.sp,
            fontWeight = if (channel.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
            color = if (channel.unreadCount > 0) Color.Black else Color.Gray
        )
        
        if (channel.unreadCount > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6B4EFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DirectMessageItem(user: UserEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO: Navigate to direct message */ }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9E8CFF),
                            Color(0xFF7B6AF9)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Username
        Text(
            text = user.username,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        // Online indicator
        if (user.isOnline) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CD964))
            )
        }
    }
}

@Composable
private fun ExpandableSectionHeader(
    title: String,
    isExpanded: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color(0xFF6B4EFF),
            modifier = Modifier.size(22.dp)
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BottomNavigationBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home icon
            BottomNavItem(
                icon = Icons.Default.Home,
                isSelected = true
            )
            
            // Chat icon
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.Send,
                isSelected = false
            )
            
            // Grid icon
            BottomNavItem(
                icon = Icons.Default.GridView,
                isSelected = false
            )
            
            // Profile icon
            BottomNavItem(
                icon = Icons.Default.Person,
                isSelected = false
            )
            
            // Settings icon
            BottomNavItem(
                icon = Icons.Default.Settings,
                isSelected = false
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFFEFEAFF) else Color.Transparent)
            .clickable { /* TODO: Handle navigation */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF6B4EFF) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
} 