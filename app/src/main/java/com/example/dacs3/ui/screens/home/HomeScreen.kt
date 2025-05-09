package com.example.dacs3.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dacs3.data.MockData
import com.example.dacs3.models.*
import com.example.dacs3.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

sealed class HomeSection(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Notifications : HomeSection(
        "Notifications",
        Icons.Outlined.Notifications,
        Icons.Filled.Notifications
    )
    object Tasks : HomeSection(
        "Tasks",
        Icons.Outlined.Assignment,
        Icons.Filled.Assignment
    )
    object Messages : HomeSection(
        "Messages",
        Icons.Outlined.Chat,
        Icons.Filled.Chat
    )
    object Workspaces : HomeSection(
        "Workspaces",
        Icons.Outlined.Workspaces,
        Icons.Filled.Workspaces
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToWorkspace: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedSection by remember { mutableStateOf<HomeSection>(HomeSection.Tasks) }
    val homeState by viewModel.homeState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedSection.title) },
                actions = {
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* TODO: Implement settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf(
                    HomeSection.Notifications,
                    HomeSection.Tasks,
                    HomeSection.Messages,
                    HomeSection.Workspaces
                ).forEach { section ->
                    NavigationBarItem(
                        selected = selectedSection == section,
                        onClick = { selectedSection = section },
                        icon = {
                            Icon(
                                imageVector = if (selectedSection == section) section.selectedIcon else section.icon,
                                contentDescription = section.title
                            )
                        },
                        label = { Text(section.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedSection) {
            HomeSection.Notifications -> NotificationsSection(
                notifications = homeState.homeResponse?.notifications ?: emptyList(),
                modifier = Modifier.padding(paddingValues)
            )
            HomeSection.Tasks -> TasksSection(
                tasks = homeState.homeResponse?.tasks ?: emptyList(),
                onTaskClick = onNavigateToTask,
                modifier = Modifier.padding(paddingValues)
            )
            HomeSection.Messages -> MessagesSection(
                messages = homeState.homeResponse?.directMessages ?: emptyList(),
                onMessageClick = onNavigateToChat,
                modifier = Modifier.padding(paddingValues)
            )
            HomeSection.Workspaces -> WorkspacesSection(
                workspaces = homeState.homeResponse?.workspaces ?: emptyList(),
                onWorkspaceClick = onNavigateToWorkspace,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun NotificationsSection(
    notifications: List<Notification>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notifications) { notification ->
            NotificationItem(notification)
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification) {
    val (backgroundColor, icon, iconTint) = when (notification.type) {
        NotificationType.TASK_ASSIGNED -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            Icons.Outlined.Assignment,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        NotificationType.TASK_COMPLETED -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            Icons.Outlined.CheckCircle,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        NotificationType.MESSAGE_RECEIVED -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            Icons.Outlined.Message,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        NotificationType.WORKSPACE_INVITE -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            Icons.Outlined.Group,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        NotificationType.SYSTEM -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            Icons.Outlined.Info,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint
            )
            Column {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = iconTint
                )
                Text(
                    text = notification.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = iconTint
                )
                Text(
                    text = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                        .format(Date(notification.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = iconTint.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TasksSection(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCompleted by remember { mutableStateOf(false) }
    val filteredTasks = tasks.filter { it.status == TaskStatus.COMPLETED == showCompleted }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                FilterChip(
                    selected = !showCompleted,
                    onClick = { showCompleted = false },
                    label = { Text("Active") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = showCompleted,
                    onClick = { showCompleted = true },
                    label = { Text("Completed") }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTasks) { task ->
                TaskItem(task = task, onTaskClick = onTaskClick)
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task.id) },
        colors = CardDefaults.cardColors(
            containerColor = when (task.priority) {
                TaskPriority.URGENT -> MaterialTheme.colorScheme.errorContainer
                TaskPriority.HIGH -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                TaskStatusChip(task.status)
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = task.assignee?.avatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = task.assignee?.name ?: "Unassigned",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = task.dueDate?.let { 
                                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(it))
                            } ?: "No due date",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { /* TODO: Implement edit */ }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { /* TODO: Implement delete */ }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun MessagesSection(
    messages: List<DirectMessage>,
    onMessageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showArchived by remember { mutableStateOf(false) }
    val filteredMessages = messages.filter { it.isArchived == showArchived }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Messages",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                FilterChip(
                    selected = !showArchived,
                    onClick = { showArchived = false },
                    label = { Text("Active") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = showArchived,
                    onClick = { showArchived = true },
                    label = { Text("Archived") }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredMessages) { message ->
                DirectMessageItem(message = message, onMessageClick = onMessageClick)
            }
        }
    }
}

@Composable
private fun DirectMessageItem(
    message: DirectMessage,
    onMessageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMessageClick(message.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (message.unreadCount > 0)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with online status
                    Box {
                        AsyncImage(
                            model = if (message.isGroup) null else message.participants.firstOrNull()?.avatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        if (!message.isGroup && message.participants.firstOrNull()?.status == UserStatus.ONLINE) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            ) {}
                        }
                    }
                    
                    Column {
                        Text(
                            text = if (message.isGroup) "Group Chat" else message.participants.firstOrNull()?.name ?: "",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = message.lastMessage?.content ?: "No messages yet",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (expanded) Int.MAX_VALUE else 1
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = message.lastMessage?.timestamp?.let {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
                        } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (message.unreadCount > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = message.unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { /* TODO: Implement archive */ }
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Archive")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { /* TODO: Implement mute */ }
                    ) {
                        Icon(Icons.Default.NotificationsOff, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mute")
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkspacesSection(
    workspaces: List<Workspace>,
    onWorkspaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showArchived by remember { mutableStateOf(false) }
    val filteredWorkspaces = workspaces.filter { it.isArchived == showArchived }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workspaces",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                FilterChip(
                    selected = !showArchived,
                    onClick = { showArchived = false },
                    label = { Text("Active") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = showArchived,
                    onClick = { showArchived = true },
                    label = { Text("Archived") }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredWorkspaces) { workspace ->
                WorkspaceItem(workspace = workspace, onWorkspaceClick = onWorkspaceClick)
            }
        }
    }
}

@Composable
private fun WorkspaceItem(
    workspace: Workspace,
    onWorkspaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onWorkspaceClick(workspace.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = when (workspace.type) {
                                WorkspaceType.TEAM -> Icons.Default.Group
                                WorkspaceType.PROJECT -> Icons.Default.Folder
                                WorkspaceType.PERSONAL -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Column {
                        Text(
                            text = workspace.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = workspace.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (expanded) Int.MAX_VALUE else 2
                        )
                    }
                }
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Members section
                Column {
                    Text(
                        text = "Members",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(-8.dp)
                    ) {
                        workspace.members.take(3).forEach { member ->
                            AsyncImage(
                                model = member.avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                        if (workspace.members.size > 3) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Text(
                                    text = "+${workspace.members.size - 3}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        icon = Icons.Default.Assignment,
                        value = workspace.tasks.size.toString(),
                        label = "Tasks"
                    )
                    StatItem(
                        icon = Icons.Default.CalendarToday,
                        value = workspace.tasks.count { it.status == TaskStatus.COMPLETED }.toString(),
                        label = "Completed"
                    )
                    StatItem(
                        icon = Icons.Default.Schedule,
                        value = workspace.tasks.count { it.status == TaskStatus.IN_PROGRESS }.toString(),
                        label = "In Progress"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { /* TODO: Implement share */ }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { /* TODO: Implement archive */ }
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Archive")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TaskStatusChip(status: TaskStatus) {
    val (backgroundColor, textColor) = when (status) {
        TaskStatus.TODO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        TaskStatus.REVIEW -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        TaskStatus.DONE -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        TaskStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = backgroundColor
    ) {
        Text(
            text = status.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
}
