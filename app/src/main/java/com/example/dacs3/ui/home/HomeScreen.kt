package com.example.dacs3.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.model.User
import com.example.dacs3.ui.channels.CreateChannelDialog
import com.example.dacs3.ui.components.BottomNavigationBar
import com.example.dacs3.ui.theme.TeamNexusPurple
import com.example.dacs3.ui.workspace.CreateWorkspaceDialog
import com.example.dacs3.ui.workspace.navigateToWorkspaceDetail
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    user: User?,
    workspace: Workspace?,
    allWorkspaces: List<Workspace> = emptyList(),
    channels: List<Channel>,
    unreadChannels: List<Channel>,
    notification: String,
    onChannelClick: (String) -> Unit,
    onAddChannel: () -> Unit,
    onclickCreateChannel: (name:String, description:String, isPrivate:Boolean) -> Unit,
    onWorkspaceSelected: (String) -> Unit,
    onWorkspaceDetailClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMessageClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onProfileClick: () -> Unit,
    oncreateWorkspaceClick: (title :String, description: String) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            WorkspaceSidebar(
                currentWorkspace = workspace,
                allWorkspaces = allWorkspaces,
                onWorkspaceSelected = { workspaceId ->
                    onWorkspaceSelected(workspaceId)
                    scope.launch {
                        drawerState.close()
                    }
                },
                onClose = {
                    scope.launch {
                        drawerState.close()
                    }
                },
                onCreateWorkspace = { name, description ->
                    oncreateWorkspaceClick(name, description)
                    scope.launch {
                        drawerState.close()
                    }
                },
                onWorkspaceDetailClick = { workspaceId ->
                    onWorkspaceDetailClick(workspaceId)
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        content = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                val (header, channelSection, unreadSection, divider1, divider2, activitySection, bottomNav) = createRefs()

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .constrainAs(header) {
                            top.linkTo(parent.top, margin = 30.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(user?.avatar),
                        contentDescription = "User avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { 
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Hello ${user?.name ?: ""}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Workspace name clickable
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { 
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Text(
                                    workspace?.name ?: "Your Workspace",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = { onNotificationClick() }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color(0xFF673AB7)
                                )
                            }
                        }
                    }
                }

                // Channels section
                Column(
                    modifier = Modifier
                        .constrainAs(channelSection) {
                            top.linkTo(header.bottom, margin = 24.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Channels",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Expand channels",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    if (channels.isEmpty()) {
                        Text(
                            "No channels",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        channels.forEach { ch ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChannelClick(ch._id) }
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = "# ${ch.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Divider 1
                Divider(
                    modifier = Modifier
                        .constrainAs(divider1) {
                            top.linkTo(channelSection.bottom, margin = 12.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        },
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                // Unread section
                Column(
                    modifier = Modifier
                        .constrainAs(unreadSection) {
                            top.linkTo(divider1.bottom, margin = 12.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                ) {
                    Text(
                        "Unreads",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (unreadChannels.isEmpty()) {
                        Text(
                            "No unread",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        unreadChannels.forEach { ch ->
                            Text(
                                "# ${ch.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }

                    // Add channel button
                    var showCreateChannelDialog by remember { mutableStateOf(false) }

                    // In the unread section where the "Add channel" button is:
                    Text(
                        "+ Add channel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier
                            .clickable { showCreateChannelDialog = true }
                            .padding(vertical = 6.dp)
                    )

                    // Add at the end of the content:
                    if (showCreateChannelDialog) {
                        CreateChannelDialog (
                            onDismiss = { showCreateChannelDialog = false },
                            onCreateChannel = { name, description, isPrivate ->
                                onclickCreateChannel(name, description, isPrivate)
                            }
                        )
                    }
                }

                // Divider 2
                Divider(
                    modifier = Modifier
                        .constrainAs(divider2) {
                            top.linkTo(unreadSection.bottom, margin = 12.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        },
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                // Activity stream
                Column(
                    modifier = Modifier
                        .constrainAs(activitySection) {
                            top.linkTo(divider2.bottom, margin = 12.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Activity stream",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Expand activity",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    if (notification.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "No notification!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // Hiển thị thông báo với dấu chấm xanh nếu chưa đọc
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon thông báo
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(TeamNexusPurple.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = TeamNexusPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Box(modifier = Modifier.weight(1f)) {
                                    // Nội dung thông báo
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = notification,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        // Dấu chấm xanh hiển thị cho thông báo chưa đọc
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(color = Color(0xFF4CAF50), shape = CircleShape)
                                        )
                                    }
                                }
                                
                                // Nút xem thêm thông báo
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "View all notifications",
                                    tint = TeamNexusPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation
                BottomNavigationBar(
                    currentRoute = "home",
                    onHomeClick = onHomeClick,
                    onMessageClick = onMessageClick,
                    onDashboardClick = onDashboardClick,
                    onProfileClick = onProfileClick,
                    modifier = Modifier
                        .constrainAs(bottomNav) {
                            bottom.linkTo(parent.bottom, margin = -50.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                )
                if (showDialog) {
                    CreateWorkspaceDialog(
                        onDismiss = { showDialog = false },
                        onCreateWorkspace = { name, description ->
                            oncreateWorkspaceClick(name, description)
                            showDialog = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun WorkspaceSidebar(
    currentWorkspace: Workspace?,
    allWorkspaces: List<Workspace>,
    onWorkspaceSelected: (String) -> Unit,
    onClose: () -> Unit,
    onCreateWorkspace: (name: String, description: String) -> Unit,
    onWorkspaceDetailClick: (String) -> Unit = {}
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your Workspaces",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close sidebar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val sortedWorkspaces = allWorkspaces
                .sortedByDescending { it.created_at }
                
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(sortedWorkspaces) { workspace ->
                    WorkspaceItem(
                        workspace = workspace,
                        isSelected = workspace._id == currentWorkspace?._id,
                        onClick = { onWorkspaceSelected(workspace._id) },
                        onDetailClick = { onWorkspaceDetailClick(workspace._id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create workspace button
            OutlinedButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Create New Workspace")
            }
        }

        // Create workspace dialog
        if (showCreateDialog) {
            CreateWorkspaceDialog(
                onDismiss = { showCreateDialog = false },
                onCreateWorkspace = { name, description ->
                    onCreateWorkspace(name, description)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun WorkspaceItem(
    workspace: Workspace,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) Color(0xFFEEE6FF) else Color.Transparent)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        // Workspace avatar/icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF673AB7)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = workspace.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Workspace name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = workspace.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            workspace.description?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }
        
        // Arrow icon to navigate to workspace detail
        IconButton(
            onClick = onDetailClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to workspace detail",
                tint = Color(0xFF673AB7)
            )
        }
    }
}
