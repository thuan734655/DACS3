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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.local.ChannelEntity
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.TaskEntity
import com.example.dacs3.data.local.UserEntity
import com.example.dacs3.data.local.WorkspaceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WorkspaceViewModel = hiltViewModel(),
    onNavigateToWorkspaces: () -> Unit = {},
    onNavigateToDirectMessage: (String) -> Unit = {},
    onNavigateToChannel: (String) -> Unit = {},
    onNavigateToTask: (String) -> Unit = {},
    onNavigateToKanban: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    userId: String? = null // This would be from login/auth flow
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val workspaces by viewModel.userWorkspaces.collectAsState(initial = emptyList())
    val selectedWorkspace by viewModel.selectedWorkspace.collectAsState()
    val channels by viewModel.channels.collectAsState(initial = emptyList())
    val workspaceChannels by viewModel.workspaceChannels.collectAsState(initial = emptyList())
    val directMessageContacts by viewModel.directMessageContacts.collectAsState()
    val unreadCount by viewModel.unreadMessageCount.collectAsState()
    val tasks by viewModel.userTasks.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }
    var showCreateWorkspaceDialog by remember { mutableStateOf(false) }
    var showCreateChannelDialog by remember { mutableStateOf(false) }
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showCreateEpicDialog by remember { mutableStateOf(false) }
    var showWorkspaceSelectorDialog by remember { mutableStateOf(false) }
    var selectedWorkspaceForChannel by remember { mutableStateOf<WorkspaceEntity?>(null) }

    // Set user ID for fetching data
    LaunchedEffect(userId) {
        userId?.let {
            viewModel.setCurrentUser(it)
        }
    }
    
    // Show error messages
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // In a real app, you'd show a Toast or Snackbar here
            // For now, we'll just clear the error
            viewModel.clearError()
        }
    }

    // Workspace selector dialog
    if (showWorkspaceSelectorDialog) {
        WorkspaceSelectorDialog(
            workspaces = workspaces,
            selectedWorkspaceId = viewModel.selectedWorkspaceId.collectAsState().value,
            onDismiss = { showWorkspaceSelectorDialog = false },
            onWorkspaceSelected = { workspaceId ->
                viewModel.selectWorkspace(workspaceId)
                showWorkspaceSelectorDialog = false
            },
            onCreateWorkspace = { 
                showWorkspaceSelectorDialog = false
                showCreateWorkspaceDialog = true 
            }
        )
    }
    
    // Create workspace dialog
    if (showCreateWorkspaceDialog) {
        CreateWorkspaceDialog(
            onDismiss = { showCreateWorkspaceDialog = false },
            onCreateWorkspace = { name, description ->
                viewModel.createWorkspace(name, description)
                showCreateWorkspaceDialog = false
            }
        )
    }
    
    // Create epic dialog
    if (showCreateEpicDialog) {
        CreateEpicDialog(
            workspaces = workspaces,
            onDismiss = { showCreateEpicDialog = false },
            onCreateEpic = { workspaceId, name, description, priority ->
                viewModel.createEpic(workspaceId, name, description, priority)
                showCreateEpicDialog = false
            },
            viewModel = viewModel
        )
    }
    
    // Create channel dialog
    if (showCreateChannelDialog && selectedWorkspaceForChannel != null) {
        CreateChannelDialog(
            workspace = selectedWorkspaceForChannel!!,
            onDismiss = { 
                showCreateChannelDialog = false
                selectedWorkspaceForChannel = null
            },
            onCreateChannel = { name, description, isPrivate ->
                viewModel.createChannel(
                    selectedWorkspaceForChannel!!.workspaceId,
                    name,
                    description,
                    isPrivate
                )
                showCreateChannelDialog = false
                selectedWorkspaceForChannel = null
            }
        )
    }
    
    // Create task dialog
    if (showCreateTaskDialog) {
        CreateTaskDialog(
            workspaces = workspaces,
            onDismiss = { showCreateTaskDialog = false },
            onCreateTask = { name, description, epicId, priority ->
                viewModel.createTask(epicId, name, description, priority)
                showCreateTaskDialog = false
            },
            viewModel = viewModel
        )
    }

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
                    workspace = selectedWorkspace,
                    onWorkspaceClick = { showWorkspaceSelectorDialog = true },
                    onNotificationClick = onNavigateToNotifications
                )
                
                when (selectedTab) {
                    HomeTab.HOME -> HomeContent(
                        tasks = tasks,
                        channels = workspaceChannels,
                        directMessageContacts = directMessageContacts,
                        onTaskClick = onNavigateToTask,
                        onChannelClick = onNavigateToChannel,
                        onDirectMessageClick = onNavigateToDirectMessage,
                        onWorkspaceClick = onNavigateToWorkspaces,
                        onCreateTask = { showCreateTaskDialog = true },
                        onCreateEpic = { showCreateEpicDialog = true }
                    )
                    HomeTab.CHAT -> ChatContent(
                        channels = workspaceChannels,
                        directMessageContacts = directMessageContacts,
                        workspaces = workspaces,
                        onChannelClick = onNavigateToChannel,
                        onDirectMessageClick = onNavigateToDirectMessage,
                        onCreateChannel = { workspace ->
                            selectedWorkspaceForChannel = workspace
                            showCreateChannelDialog = true
                        }
                    )
                    HomeTab.PROJECTS -> ProjectsContent(
                        workspaces = workspaces,
                        onWorkspaceClick = onNavigateToWorkspaces,
                        onKanbanClick = onNavigateToKanban,
                        onCreateWorkspace = { showCreateWorkspaceDialog = true },
                        onCreateEpic = { showCreateEpicDialog = true },
                        onCreateTask = { showCreateTaskDialog = true },
                        viewModel = viewModel
                    )
                    HomeTab.PROFILE -> ProfilePreview(
                        user = currentUser,
                        onProfileClick = onNavigateToProfile
                    )
                }
                
                // Spacer to push bottom nav to bottom
                Spacer(modifier = Modifier.weight(1f))
                
                // Bottom Navigation
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    tasks: List<TaskEntity>,
    channels: List<ChannelEntity>,
    directMessageContacts: List<UserEntity>,
    onTaskClick: (String) -> Unit,
    onChannelClick: (String) -> Unit,
    onDirectMessageClick: (String) -> Unit,
    onWorkspaceClick: () -> Unit,
    onCreateTask: () -> Unit,
    onCreateEpic: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Task progress card or create task button
        item {
            if (tasks.isEmpty()) {
                NoTasksCard(
                    onCreateTask = onCreateTask,
                    onCreateEpic = onCreateEpic
                )
            } else {
                tasks.firstOrNull()?.let { task ->
                    TaskProgressCard(
                        task = task,
                        onClick = { onTaskClick(task.taskId) }
                    )
                }
            }
        }
        
        // Quick Actions
        item {
            QuickActions(
                onWorkspacesClick = onWorkspaceClick,
                onKanbanClick = { /* Navigate to Kanban */ },
                onTasksClick = onCreateTask,
                onEpicsClick = onCreateEpic,
                onBugsClick = { /* Navigate to Bugs */ }
            )
        }
                
        // Channels section 
        item {
            ChannelsSection(
                channels = channels,
                onChannelClick = onChannelClick
            )
        }
        
        // Direct Messages section
        item {
            DirectMessagesSection(
                users = directMessageContacts,
                onUserClick = onDirectMessageClick
            )
        }
    }
}

@Composable
private fun NoTasksCard(
    onCreateTask: () -> Unit,
    onCreateEpic: () -> Unit
) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tasks yet",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Create an epic and tasks to get started",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Create Epic button
                Button(
                    onClick = onCreateEpic,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = Color(0xFF6B4EFF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Epic",
                        color = Color(0xFF6B4EFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Create Task button
                Button(
                    onClick = onCreateTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF6B4EFF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Task",
                        color = Color(0xFF6B4EFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatContent(
    channels: List<ChannelEntity>,
    directMessageContacts: List<UserEntity>,
    workspaces: List<WorkspaceEntity>,
    onChannelClick: (String) -> Unit,
    onDirectMessageClick: (String) -> Unit,
    onCreateChannel: (WorkspaceEntity) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Messages",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF6B4EFF)
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Direct Messages") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Channels") }
            )
        }
        
        when (selectedTabIndex) {
            0 -> {
                // Direct Messages tab
                if (directMessageContacts.isEmpty()) {
                    EmptyStateMessage(
                        message = "No direct message contacts yet",
                        icon = Icons.Default.Person
                    )
                } else {
                    LazyColumn {
                        items(directMessageContacts) { user ->
                            DirectMessageItem(
                                user = user,
                                onClick = { onDirectMessageClick(user.userId) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Channels tab
                if (channels.isEmpty()) {
                    if (workspaces.isEmpty()) {
                        EmptyStateMessage(
                            message = "Create a workspace to add channels",
                            icon = Icons.Default.Business
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No channels yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            workspaces.forEach { workspace ->
                                Button(
                                    onClick = { onCreateChannel(workspace) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6B4EFF)
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Create channel in ${workspace.name}")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn {
                        items(channels) { channel ->
                            ChannelItem(
                                channel = channel,
                                onClick = { onChannelClick(channel.channelId) }
                            )
                        }
                        
                        item {
                            if (workspaces.isNotEmpty()) {
                                Button(
                                    onClick = { onCreateChannel(workspaces.first()) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6B4EFF)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Create new channel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProjectsContent(
    workspaces: List<WorkspaceEntity>,
    onWorkspaceClick: () -> Unit,
    onKanbanClick: () -> Unit,
    onCreateWorkspace: () -> Unit,
    onCreateEpic: () -> Unit,
    onCreateTask: () -> Unit,
    viewModel: WorkspaceViewModel = hiltViewModel()
) {
    val epics by viewModel.epics.collectAsState()
    val selectedWorkspaceId by viewModel.selectedWorkspaceId.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Projects & Tasks",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        
        // Kanban card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onKanbanClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEDE9FF)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ViewKanban,
                    contentDescription = null,
                    tint = Color(0xFF6B4EFF),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Kanban Board",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Manage your tasks visually",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Workspace section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Workspaces",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            IconButton(onClick = onCreateWorkspace) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Workspace",
                    tint = Color(0xFF6B4EFF)
                )
            }
        }
        
        // Workspace list with hierarchy
        if (workspaces.isEmpty()) {
            EmptyStateMessage(
                message = "No workspaces yet\nCreate your first workspace to get started",
                icon = Icons.Default.Business
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Flatten the structure to avoid nesting items
                // First, add all workspaces
                workspaces.forEach { workspace ->
                    val isSelected = workspace.workspaceId == selectedWorkspaceId
                    
                    item(key = "workspace-${workspace.workspaceId}") {
                        // Workspace item
                        ProjectHierarchyItem(
                            name = workspace.name,
                            description = workspace.description,
                            icon = Icons.Default.Business,
                            color = Color(0xFF6B4EFF),
                            isExpanded = isSelected,
                            onClick = { viewModel.selectWorkspace(workspace.workspaceId) },
                            onCreateChild = onCreateEpic
                        )
                    }
                    
                    // Show epics if workspace is selected
                    if (isSelected) {
                        val workspaceEpics = epics.filter { it.workspaceId == workspace.workspaceId }
                        
                        // Add a message if there are no epics
                        if (workspaceEpics.isEmpty()) {
                            item(key = "no-epics-${workspace.workspaceId}") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 56.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "No epics yet. Create one to organize your tasks.",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            // Add each epic under this workspace
                            workspaceEpics.forEach { epic ->
                                val isEpicSelected = epic.epicId == viewModel.selectedEpicId.value
                                
                                item(key = "epic-${epic.epicId}") {
                                    // Epic item with indentation
                                    Box(
                                        modifier = Modifier.padding(start = 32.dp)
                                    ) {
                                        ProjectHierarchyItem(
                                            name = epic.name,
                                            description = epic.description,
                                            icon = Icons.Default.Assignment,
                                            color = Color(0xFF9C27B0),
                                            isExpanded = isEpicSelected,
                                            onClick = { viewModel.selectEpic(epic.epicId) },
                                            onCreateChild = onCreateTask
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Create workspace button
                item(key = "create-workspace-button") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCreateWorkspace() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Workspace",
                            tint = Color(0xFF6B4EFF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Create New Workspace",
                            color = Color(0xFF6B4EFF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectHierarchyItem(
    name: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onCreateChild: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    
                    if (description.isNotBlank()) {
                        Text(
                            text = description,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
                
                // Expand/collapse icon
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = color
                    )
                }
            }
            
            // Add button for child items
            if (isExpanded) {
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCreateChild)
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add item",
                        color = color,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePreview(
    user: UserEntity?,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        user?.let {
            Box(
                modifier = Modifier
                    .size(96.dp)
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
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = user.username,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onProfileClick,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("View Profile")
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun WorkspaceItem(
    workspace: WorkspaceEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6B4EFF),
                                Color(0xFF9E8CFF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = workspace.name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = workspace.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = workspace.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun QuickActions(
    onWorkspacesClick: () -> Unit,
    onKanbanClick: () -> Unit,
    onTasksClick: () -> Unit,
    onEpicsClick: () -> Unit,
    onBugsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionButton(
            icon = Icons.Default.Business,
            label = "Workspaces",
            color = Color(0xFF6B4EFF),
            onClick = onWorkspacesClick
        )
        
        ActionButton(
            icon = Icons.Default.Assignment,
            label = "Epics",
            color = Color(0xFF9C27B0),
            onClick = onEpicsClick
        )
        
        ActionButton(
            icon = Icons.Default.Task,
            label = "Tasks",
            color = Color(0xFFFFA726),
            onClick = onTasksClick
        )
        
        ActionButton(
            icon = Icons.Default.BugReport,
            label = "Bugs",
            color = Color(0xFFEF5350),
            onClick = onBugsClick
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
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
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
private fun WorkspaceHeader(
    workspace: WorkspaceEntity?,
    modifier: Modifier = Modifier,
    onWorkspaceClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onWorkspaceClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Workspace icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6B4EFF),
                            Color(0xFF9E8CFF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (workspace != null) {
                Text(
                    text = workspace.name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            } else {
            Icon(
                    imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            }
        }
        
        // Workspace info
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = workspace?.name ?: "Select Workspace",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = workspace?.description ?: "Tap to choose a workspace",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Dropdown indicator
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Select workspace",
            tint = Color(0xFF6B4EFF),
            modifier = Modifier.padding(end = 12.dp)
        )
        
        // Notification icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF6B4EFF))
                .clickable(onClick = onNotificationClick)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WorkspaceSelectorDialog(
    workspaces: List<WorkspaceEntity>,
    selectedWorkspaceId: String?,
    onDismiss: () -> Unit,
    onWorkspaceSelected: (String) -> Unit,
    onCreateWorkspace: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Workspace") },
        text = {
    Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (workspaces.isEmpty()) {
                    Text(
                        text = "No workspaces available. Create a new one to get started.",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn {
                        items(workspaces) { workspace ->
                            WorkspaceSelectionItem(
                                workspace = workspace,
                                isSelected = workspace.workspaceId == selectedWorkspaceId,
                                onClick = { onWorkspaceSelected(workspace.workspaceId) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onCreateWorkspace,
        modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create New Workspace")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun WorkspaceSelectionItem(
    workspace: WorkspaceEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Workspace icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6B4EFF),
                            Color(0xFF9E8CFF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = workspace.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Workspace info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = workspace.name,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = workspace.description,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Selected indicator
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF6B4EFF)
            )
        }
    }
}

@Composable
private fun TaskProgressCard(
    task: TaskEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .heightIn(min = 120.dp)
            .clickable(onClick = onClick),
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
                        onClick = onClick,
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
    unreadChannels: List<ChannelEntity>,
    onChannelClick: (String) -> Unit
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
                ChannelItem(
                    channel = channel,
                    onClick = { onChannelClick(channel.channelId) }
                )
            }
        } else {
            // Show a placeholder channel to match design
            ChannelItem(
                channel = ChannelEntity(
                    channelId = "placeholder",
                    name = "abc-xyz",
                    description = "Placeholder",
                    workspaceId = "workspace1",
                    createdBy = "user1",
                    isPrivate = false,
                    unreadCount = 0
                ),
                onClick = {}
            )
        }
    }
}

@Composable
private fun ChannelsSection(
    channels: List<ChannelEntity>,
    onChannelClick: (String) -> Unit
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
        
        if (channels.isEmpty()) {
            Text(
                text = "No channels in this workspace yet",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        } else {
        channels.forEach { channel ->
                ChannelItem(
                    channel = channel,
                    onClick = { onChannelClick(channel.channelId) }
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
    users: List<UserEntity>,
    onUserClick: (String) -> Unit
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
        
        // Display actual users instead of mock data
        if (users.isEmpty()) {
        Text(
                text = "No direct messages yet",
            fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        } else {
            users.forEach { user ->
                DirectMessageItem(
                    user = user,
                    onClick = { onUserClick(user.userId) }
                )
            }
        }
    }
}

@Composable
private fun ChannelItem(
    channel: ChannelEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
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
            color = if (channel.unreadCount > 0) Color.Black else Color.Gray,
            modifier = Modifier.weight(1f)
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
private fun DirectMessageItem(
    user: UserEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
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
            color = Color.Black,
            modifier = Modifier.weight(1f)
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
private fun BottomNavigationBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
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
                isSelected = selectedTab == HomeTab.HOME,
                onClick = { onTabSelected(HomeTab.HOME) }
            )
            
            // Chat icon
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.Send,
                isSelected = selectedTab == HomeTab.CHAT,
                onClick = { onTabSelected(HomeTab.CHAT) }
            )
            
            // Projects icon
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                isSelected = selectedTab == HomeTab.PROJECTS,
                onClick = { onTabSelected(HomeTab.PROJECTS) }
            )
            
            // Profile icon
            BottomNavItem(
                icon = Icons.Default.Person,
                isSelected = selectedTab == HomeTab.PROFILE,
                onClick = { onTabSelected(HomeTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFFEFEAFF) else Color.Transparent)
            .clickable(onClick = onClick),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateWorkspaceDialog(
    onDismiss: () -> Unit,
    onCreateWorkspace: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Workspace") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Workspace Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank()) {
                        onCreateWorkspace(name, description) 
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateChannelDialog(
    workspace: WorkspaceEntity,
    onDismiss: () -> Unit,
    onCreateChannel: (name: String, description: String, isPrivate: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Channel in ${workspace.name}") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Channel Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                    Text("Private Channel")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank()) {
                        onCreateChannel(name, description, isPrivate) 
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskDialog(
    workspaces: List<WorkspaceEntity>,
    onDismiss: () -> Unit,
    onCreateTask: (name: String, description: String, epicId: String, priority: Int) -> Unit,
    viewModel: WorkspaceViewModel = hiltViewModel()
) {
    val selectedWorkspaceId by viewModel.selectedWorkspaceId.collectAsState()
    val selectedEpicId by viewModel.selectedEpicId.collectAsState()
    val epics by viewModel.epics.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(3) } // Medium priority by default
    
    var selectedWorkspace by remember { mutableStateOf<WorkspaceEntity?>(null) }
    var selectedEpic by remember { mutableStateOf<EpicEntity?>(null) }
    var expandedWorkspace by remember { mutableStateOf(false) }
    var expandedEpic by remember { mutableStateOf(false) }
    
    // Update values when selection changes in ViewModel
    LaunchedEffect(selectedWorkspaceId) {
        selectedWorkspaceId?.let { wsId ->
            selectedWorkspace = workspaces.find { it.workspaceId == wsId }
        }
    }
    
    LaunchedEffect(selectedEpicId) {
        selectedEpicId?.let { epId ->
            selectedEpic = epics.find { it.epicId == epId }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Task") },
        text = {
            Column {
                // Workspace selection
                Column {
                    Text("Select Workspace", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box {
                        OutlinedTextField(
                            value = selectedWorkspace?.name ?: "Select Workspace",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedWorkspace = true },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        )
                        
                        DropdownMenu(
                            expanded = expandedWorkspace,
                            onDismissRequest = { expandedWorkspace = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            workspaces.forEach { workspace ->
                                DropdownMenuItem(
                                    text = { Text(workspace.name) },
                                    onClick = {
                                        viewModel.selectWorkspace(workspace.workspaceId)
                                        selectedWorkspace = workspace
                                        expandedWorkspace = false
                                        // Reset epic selection when workspace changes
                                        selectedEpic = null
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Epic selection (only enabled if workspace is selected)
                Column {
                    Text("Select Epic", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box {
                        OutlinedTextField(
                            value = selectedEpic?.name ?: "Select Epic",
                            onValueChange = {},
                            readOnly = true,
                            enabled = selectedWorkspace != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = selectedWorkspace != null) { 
                                    expandedEpic = true 
                                },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        )
                        
                        DropdownMenu(
                            expanded = expandedEpic && selectedWorkspace != null,
                            onDismissRequest = { expandedEpic = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            if (epics.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No epics - Create one first") },
                                    onClick = { expandedEpic = false }
                                )
                            } else {
                                epics.forEach { epic ->
                                    DropdownMenuItem(
                                        text = { Text(epic.name) },
                                        onClick = {
                                            viewModel.selectEpic(epic.epicId)
                                            selectedEpic = epic
                                            expandedEpic = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Task name
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Task description
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Priority selection
                Text("Priority", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(1, 2, 3, 4, 5).forEach { p ->
                        PriorityButton(
                            priority = p,
                            selected = priority == p,
                            onClick = { priority = p }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank() && selectedEpic != null) {
                        onCreateTask(name, description, selectedEpic!!.epicId, priority) 
                    }
                },
                enabled = name.isNotBlank() && selectedEpic != null
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEpicDialog(
    workspaces: List<WorkspaceEntity>,
    onDismiss: () -> Unit,
    onCreateEpic: (workspaceId: String, name: String, description: String, priority: Int) -> Unit,
    viewModel: WorkspaceViewModel = hiltViewModel()
) {
    val selectedWorkspaceId by viewModel.selectedWorkspaceId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(3) } // Medium priority by default
    var expandedWorkspace by remember { mutableStateOf(false) }
    
    var selectedWorkspace by remember { mutableStateOf<WorkspaceEntity?>(null) }
    
    // Filter workspaces where current user is a leader
    val leaderWorkspaces = remember(workspaces, currentUser) {
        workspaces.filter { workspace ->
            workspace.createdBy == currentUser?.userId
        }
    }
    
    // Update values when selection changes in ViewModel
    LaunchedEffect(selectedWorkspaceId) {
        selectedWorkspaceId?.let { wsId ->
            selectedWorkspace = leaderWorkspaces.find { it.workspaceId == wsId }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Epic") },
        text = {
            Column {
                // Workspace selection
                Column {
                    Text("Select Workspace", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box {
                        OutlinedTextField(
                            value = selectedWorkspace?.name ?: "Select Workspace",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedWorkspace = true },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        )
                        
                        DropdownMenu(
                            expanded = expandedWorkspace,
                            onDismissRequest = { expandedWorkspace = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            if (leaderWorkspaces.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No workspaces found where you are leader") },
                                    onClick = { expandedWorkspace = false }
                                )
                            } else {
                                leaderWorkspaces.forEach { workspace ->
                                    DropdownMenuItem(
                                        text = { Text(workspace.name) },
                                        onClick = {
                                            viewModel.selectWorkspace(workspace.workspaceId)
                                            selectedWorkspace = workspace
                                            expandedWorkspace = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Epic name
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Epic Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Epic description
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Priority selection
                Text("Priority", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(1, 2, 3, 4, 5).forEach { p ->
                        PriorityButton(
                            priority = p,
                            selected = priority == p,
                            onClick = { priority = p }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank() && selectedWorkspace != null) {
                        onCreateEpic(selectedWorkspace!!.workspaceId, name, description, priority) 
                    }
                },
                enabled = name.isNotBlank() && selectedWorkspace != null
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PriorityButton(
    priority: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = when (priority) {
        1 -> Color(0xFF4CD964) // Low
        2 -> Color(0xFF5AC8FA)
        3 -> Color(0xFFFFCC00) // Medium
        4 -> Color(0xFFFF9500)
        5 -> Color(0xFFFF3B30) // High
        else -> Color.Gray
    }
    
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (selected) color else color.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.toString(),
            color = if (selected) Color.White else color,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class HomeTab {
    HOME, CHAT, PROJECTS, PROFILE
} 