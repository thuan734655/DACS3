package com.example.dacs3.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.model.WorkspaceMember
import com.example.dacs3.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceDetailScreen(
    workspaceId: String,
    navController: NavController,
    viewModel: WorkspaceDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // State for invitation dialog
    var showInvitationDialog by remember { mutableStateOf(false) }
    var inviteeEmail by remember { mutableStateOf("") }
    
    // Load workspace details when screen is displayed
    LaunchedEffect(workspaceId) {
        viewModel.loadWorkspaceDetails(workspaceId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = state.data?.workspace?.name ?: "Workspace Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "workspace_detail",
                onHomeClick = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }},
                onMessageClick = { navController.navigate("conversations") },
                onDashboardClick = { navController.navigate("dashboard") },
                onProfileClick = { navController.navigate("profile") }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.error != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error ?: "Unknown error occurred",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadWorkspaceDetails(workspaceId) }
                        ) {
                            Text("Retry")
                        }
                    }
                } else {
                    state.data?.let { workspaceData ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Workspace Basic Info
                            item {
                                WorkspaceInfoCard(workspaceData)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Statistics
                            item {
                                StatisticsCard(workspaceData)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Members
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Members (${workspaceData.workspace.members?.size ?: 0})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    
                                    Button(
                                        onClick = { showInvitationDialog = true },
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.PersonAdd,
                                            contentDescription = "Mời thành viên",
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mời thành viên", color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            // Members list
                            workspaceData.workspace.members?.let { members ->
                                items(members) { member ->
                                    MemberItem(member)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            
                            // Notifications
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Recent Notifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            // Notifications list
                            workspaceData.notifications?.let { notifications ->
                                if (notifications.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No notifications",
                                            color = Color.Gray,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                } else {
                                    items(notifications) { notification ->
                                        NotificationItem(notification)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
                
                // Dialog for inviting new members
                if (showInvitationDialog) {
                    AlertDialog(
                        onDismissRequest = { 
                            showInvitationDialog = false 
                            inviteeEmail = ""
                            viewModel.resetActionState()
                        },
                        title = { Text("Mời thành viên vào Workspace") },
                        text = {
                            Column {
                                if (state.actionError != null) {
                                    Text(
                                        text = state.actionError ?: "Lỗi không xác định",
                                        color = Color.Red,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                
                                Text(
                                    text = "Người dùng sẽ nhận được email mời tham gia và thông báo trong ứng dụng.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                OutlinedTextField(
                                    value = inviteeEmail,
                                    onValueChange = { inviteeEmail = it },
                                    label = { Text("Email") },
                                    placeholder = { Text("Nhập email người được mời") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (inviteeEmail.isNotBlank()) {
                                        viewModel.sendInvitation(inviteeEmail)
                                    }
                                },
                                enabled = !state.actionInProgress && inviteeEmail.isNotBlank()
                            ) {
                                if (state.actionInProgress) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Gửi lời mời")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { 
                                    showInvitationDialog = false 
                                    inviteeEmail = ""
                                    viewModel.resetActionState()
                                }
                            ) {
                                Text("Hủy")
                            }
                        }
                    )
                }
                
                // Handle action success
                LaunchedEffect(state.actionSuccess) {
                    if (state.actionSuccess) {
                        showInvitationDialog = false
                        inviteeEmail = ""
                        viewModel.resetActionState()
                    }
                }
            }
        }
    )
}

@Composable
fun WorkspaceInfoCard(workspaceData: com.example.dacs3.data.model.WorkspaceDetailData) {
    val workspace = workspaceData.workspace
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Workspace icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF673AB7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = workspace.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = workspace.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    
                    workspace.description?.let {
                        Text(
                            text = it,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
            
            // Created by
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Created by:",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Creator avatar
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = workspace.created_by.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = workspace.created_by.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Created at
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Created at:",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = workspace.created_at.toString().substringBefore("T"),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Channels
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Channels:",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = (workspace.channels?.size ?: 0).toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StatisticsCard(workspaceData: com.example.dacs3.data.model.WorkspaceDetailData) {
    val counts = workspaceData.counts
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.People,
                    value = counts?.members?.toString() ?: "0",
                    label = "Members",
                    color = Color(0xFF673AB7)
                )
                
                StatItem(
                    icon = Icons.Default.Assignment,
                    value = counts?.epics?.toString() ?: "0",
                    label = "Epics",
                    color = Color(0xFF2196F3)
                )
                
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    value = counts?.tasks?.toString() ?: "0",
                    label = "Tasks",
                    color = Color(0xFF4CAF50)
                )
                
                StatItem(
                    icon = Icons.Default.BugReport,
                    value = counts?.bugs?.toString() ?: "0",
                    label = "Bugs",
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
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
fun MemberItem(member: WorkspaceMember) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Member avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3)),
                contentAlignment = Alignment.Center
            ) {
                member.user_id.name.take(1).uppercase().let { initial ->
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Member info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = member.user_id.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            
            // Role badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (member.role) {
                            "Leader" -> Color(0xFF673AB7)
                            "Admin" -> Color(0xFF2196F3)
                            else -> Color(0xFF4CAF50)
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = member.role,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.is_read) Color.White else Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            "channel" -> Color(0xFF2196F3)
                            "task" -> Color(0xFF4CAF50)
                            "bug" -> Color(0xFFF44336)
                            else -> Color(0xFF9E9E9E)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        "channel" -> Icons.Default.Forum
                        "task" -> Icons.Default.Assignment
                        "bug" -> Icons.Default.BugReport
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = notification.type,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Notification content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.content,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.created_at.toString().substring(0, 16).replace("T", " at "),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            if (!notification.is_read) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3))
                )
            }
        }
    }
}
