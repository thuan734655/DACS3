package com.example.dacs3.ui.epic

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Epic
import com.example.dacs3.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
// Import dialog components
import com.example.dacs3.viewmodel.EpicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpicScreen(
    workspaceId: String,
    viewModel: EpicViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEpicSelected: (Epic) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var epicToUpdate by remember { mutableStateOf<Epic?>(null) }
    var epicToDelete by remember { mutableStateOf<Epic?>(null) }
    
    LaunchedEffect(workspaceId) {
        viewModel.loadEpics(workspaceId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Epics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter Epics")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Epic")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.filteredEpics.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No epics found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showCreateDialog = true }) {
                        Text("Create Epic")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(uiState.filteredEpics) { epic ->
                        EpicItem(
                            epic = epic,
                            onClick = { onEpicSelected(epic) },
                            onUpdateClick = {
                                epicToUpdate = epic
                                showUpdateDialog = true
                            },
                            onDeleteClick = {
                                epicToDelete = epic
                                showDeleteDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            // Show error message if any
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
            
            // Create Epic Dialog
            if (showCreateDialog) {
                CreateEpicDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreateEpic = { title, description, startDate, dueDate, priority, status ->
                        viewModel.createEpic(
                            title = title,
                            description = description,
                            workspaceId = workspaceId,
                            startDate = startDate,
                            dueDate = dueDate,
                            priority = priority,
                            status = status
                        )
                        showCreateDialog = false
                    }
                )
            }
            
            // Update Epic Dialog
            if (showUpdateDialog && epicToUpdate != null) {
                UpdateEpicDialog(
                    epic = epicToUpdate!!,
                    onDismiss = { 
                        showUpdateDialog = false 
                        epicToUpdate = null
                    },
                    onUpdateEpic = { id, title, description, assignedTo, status, priority, startDate, dueDate, completedDate, sprintId ->
                        viewModel.updateEpic(
                            id = id,
                            title = title,
                            description = description,
                            assignedTo = assignedTo,
                            status = status,
                            priority = priority,
                            startDate = startDate,
                            dueDate = dueDate,
                            completedDate = completedDate,
                            sprintId = sprintId
                        )
                        showUpdateDialog = false
                        epicToUpdate = null
                    }
                )
            }
            
            // Filter Dialog
            if (showFilterDialog) {
                FilterEpicDialog(
                    onDismiss = { showFilterDialog = false },
                    onApplyFilter = { status ->
                        viewModel.filterEpicsByStatus(status)
                        showFilterDialog = false
                    }
                )
            }
            
            // Delete Epic Dialog
            if (showDeleteDialog && epicToDelete != null) {
                DeleteEpicDialog(
                    epic = epicToDelete!!,
                    onDismiss = { 
                        showDeleteDialog = false 
                        epicToDelete = null
                    },
                    onConfirmDelete = { id ->
                        viewModel.deleteEpic(id)
                        showDeleteDialog = false
                        epicToDelete = null
                    }
                )
            }
            
            // Success messages
            if (uiState.isCreationSuccessful) {
                LaunchedEffect(uiState.isCreationSuccessful) {
                    // Reset the state after showing success
                    viewModel.resetCreationState()
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Epic created successfully!")
                }
            }
            
            // Update success message
            if (uiState.isUpdateSuccessful) {
                LaunchedEffect(uiState.isUpdateSuccessful) {
                    // Reset the state after showing success
                    viewModel.resetUpdateState()
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Epic updated successfully!")
                }
            }
        }
    }
}

@Composable
fun EpicItem(
    epic: Epic,
    onClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    // Determine card background gradient based on status
    val (startColor, endColor) = when(epic.status.lowercase()) {
        "to_do" -> Pair(Color(0xFFF9F8FF), Color(0xFFF0F0FF))
        "in_progress" -> Pair(Color(0xFFE6F4FF), Color(0xFFD6EBFF))
        "done" -> Pair(Color(0xFFE7F9EF), Color(0xFFDDF5E7))
        else -> Pair(Color(0xFFF5F5F5), Color(0xFFEEEEEE))
    }
    
    // Determine status border color
    val statusColor = when(epic.status.lowercase()) {
        "to_do" -> SecondaryColor
        "in_progress" -> PrimaryBlue
        "done" -> SuccessGreen
        else -> MaterialTheme.colorScheme.primary
    }
    
    // Determine priority accent color
    val priorityColor = when(epic.priority.lowercase()) {
        "high" -> ErrorRed
        "medium" -> Color(0xFFFFA726) // Orange
        "low" -> Color(0xFF66BB6A) // Green
        else -> Color(0xFF8E8E8E) // Gray for unknown
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White, // Will be covered by our gradient
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Colorful left border indicator for status
        Box(modifier = Modifier.fillMaxWidth()) {
            // Left status indicator bar
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(statusColor)
                    .align(Alignment.CenterStart)
            )
            
            // Main content with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(startColor, endColor),
                            startX = 0f,
                            endX = 1000f
                        )
                    )
                    .padding(start = 16.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header with status and priority badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side - Title and status
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = epic.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Status and priority in a column for better space management
                            Column(
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                // Status indicator
                                Row(
                                    modifier = Modifier.wrapContentWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(statusColor, shape = CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = epic.status.capitalize(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = statusColor
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                // Priority badge
                                Surface(
                                    modifier = Modifier.wrapContentWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = priorityColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, priorityColor)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when(epic.priority.lowercase()) {
                                                "high" -> Icons.Default.PriorityHigh
                                                "medium" -> Icons.Default.List
                                                else -> Icons.Default.LowPriority
                                            },
                                            contentDescription = "Priority",
                                            modifier = Modifier.size(12.dp),
                                            tint = priorityColor
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = epic.priority.capitalize(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = priorityColor
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Right side - Action buttons only
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            
                            // Edit button
                            IconButton(
                                onClick = { onUpdateClick() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Epic",
                                    tint = TeamNexusPurple
                                )
                            }
                            
                            // Delete button
                            IconButton(
                                onClick = { onDeleteClick() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Epic",
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Description
                    epic.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = TextGrey
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Creator and assigned
                        Column(modifier = Modifier.weight(1f)) {
                            // Creator info
                            epic.created_by?.let { creator ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Created by: ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextGrey
                                    )
                                    
                                    Text(
                                        text = creator.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            // Assignee info if available
                            epic.assigned_to?.let { assigneeId ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Assigned to: $assigneeId",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextGrey
                                    )
                                }
                            }
                        }
                        
                        // Dates - Start, Due, Completed
                        Column(horizontalAlignment = Alignment.End) {
                            // Start date
                            epic.start_date?.let { startDate ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Start Date",
                                        modifier = Modifier.size(14.dp),
                                        tint = TextGrey
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Start: ${dateFormat.format(startDate)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGrey
                                    )
                                }
                            }
                            
                            // Due date
                            epic.due_date?.let { dueDate ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Due Date",
                                        modifier = Modifier.size(14.dp),
                                        tint = TextGrey
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Due: ${dateFormat.format(dueDate)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGrey
                                    )
                                }
                            }
                            
                            // Completed date (only if status is done)
                            if (epic.status.lowercase() == "done") {
                                epic.completed_date?.let { completedDate ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = "Completed Date",
                                            modifier = Modifier.size(14.dp),
                                            tint = SuccessGreen
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Completed: ${dateFormat.format(completedDate)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SuccessGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
