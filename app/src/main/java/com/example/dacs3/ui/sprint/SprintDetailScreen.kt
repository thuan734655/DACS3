package com.example.dacs3.ui.sprint

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dacs3.data.model.Task
import com.example.dacs3.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintDetailScreen(
    sprintId: String,
    viewModel: SprintViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    // Find current sprint from the list
    val currentSprint = uiState.sprints.find { it._id == sprintId }
    val tasks = uiState.sprintTasks[sprintId] ?: emptyList()
    
    // Format date
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    val context = LocalContext.current
    
    // Load sprint details when the screen is first displayed
    LaunchedEffect(sprintId) {
        viewModel.loadSprintDetail(sprintId)
    }
    
    // Handle deletion success
    LaunchedEffect(uiState.isDeletionSuccessful) {
        if (uiState.isDeletionSuccessful) {
            Toast.makeText(context, "Sprint deleted successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetDeletionState()
            onNavigateBack()
        }
    }
    
    // Theo dõi trạng thái cập nhật sprint
    LaunchedEffect(uiState.isUpdateSuccessful) {
        if (uiState.isUpdateSuccessful) {
            Toast.makeText(context, "Sprint updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentSprint?.name ?: "Sprint Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Edit button
                    IconButton(onClick = {
                        sprintId?.let { id ->
                            navController.navigate(Screen.EditSprint.createRoute(id))
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Sprint"
                        )
                    }
                    
                    // Delete button
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Sprint",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (currentSprint == null) {
                Text(
                    text = "Sprint information not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Sprint Information
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = currentSprint.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Status
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    val statusColor = when(currentSprint.status) {
                                        "TO_DO" -> Color(0xFF9E9E9E)
                                        "IN_PROGRESS" -> Color(0xFF2196F3)
                                        "DONE" -> Color(0xFF4CAF50)
                                        else -> Color(0xFF9E9E9E)
                                    }
                                    
                                    // Status menu dropdown
                                    var expanded by remember { mutableStateOf(false) }
                                    val statusOptions = listOf("TO_DO", "IN_PROGRESS", "DONE")
                                    
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = statusColor.copy(alpha = 0.1f),
                                        modifier = Modifier
                                            .padding(start = 4.dp)
                                            .clickable { expanded = true }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = when(currentSprint.status) {
                                                    "TO_DO" -> "To DO"
                                                    "IN_PROGRESS" -> "In Progress"
                                                    "DONE" -> "Completed"
                                                    else -> currentSprint.status
                                                },
                                                color = statusColor,
                                                fontSize = 14.sp
                                            )
                                            
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Change status",
                                                tint = statusColor,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                        
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            statusOptions.forEach { status ->
                                                DropdownMenuItem(
                                                    text = { 
                                                        Text(
                                                            text = when(status) {
                                                                "TO_DO" -> "To Do"
                                                                "IN_PROGRESS" -> "In Progress"
                                                                "DONE" -> "Completed"
                                                                else -> status
                                                            }
                                                        ) 
                                                    },
                                                    onClick = {
                                                        // Chỉ cập nhật nếu trạng thái thay đổi
                                                        if (status != currentSprint.status) {
                                                            android.util.Log.d("SprintDetailScreen", "Updating sprint status: ${currentSprint._id} from ${currentSprint.status} to $status")
                                                            // Thêm log chi tiết để debug
                                                            android.util.Log.d("SprintDetailScreen", "Status value ENUM being sent to API: $status")
                                                            viewModel.updateSprintStatus(currentSprint._id, status)
                                                        } else {
                                                            android.util.Log.d("SprintDetailScreen", "Status unchanged, not updating")
                                                        }
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Description
                                if (!currentSprint.description.isNullOrEmpty()) {
                                    Text(
                                        text = "Created by:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = currentSprint.description ?: "No description",
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                // Goal
                                if (!currentSprint.goal.isNullOrEmpty()) {
                                    Text(
                                        text = "Goal:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = currentSprint.goal ?: "No goal specified",
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                // Timeline
                                Text(
                                    text = "Timeline:",
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${dateFormat.format(currentSprint.start_date)} - ${dateFormat.format(currentSprint.end_date)}",
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))

                            }
                        }
                    }
                    
                    // Task list title
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tasks in Sprint (${tasks.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Button(
                                onClick = { /* Navigate to create task page */ },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add new task"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add new task")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Task list
                    if (tasks.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No tasks in this Sprint",
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        items(tasks) { task ->
                            TaskItemInSprint(task = task)
                        }
                    }
                }
            }
            
            // Error message
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
            
            // Delete confirmation dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Sprint") },
                    text = { Text("Are you sure you want to delete this sprint? This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                sprintId?.let { id ->
                                    viewModel.deleteSprint(id)
                                }
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItemInSprint(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task status indicator
            val statusColor = when(task.status.lowercase()) {
                "to do" -> Color(0xFF9E9E9E)
                "in progress" -> Color(0xFF2196F3)
                "review" -> Color(0xFFFFA000)
                "done" -> Color(0xFF4CAF50)
                else -> Color(0xFF9E9E9E)
            }
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(statusColor, RoundedCornerShape(2.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Task title and details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Medium
                )
                
                if (!task.description.isNullOrEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Priority indicator
            val priorityColor = when(task.priority.lowercase()) {
                "high" -> Color(0xFFE53935)
                "medium" -> Color(0xFFFFA000)
                "low" -> Color(0xFF4CAF50)
                else -> Color(0xFF9E9E9E)
            }
            
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = priorityColor.copy(alpha = 0.1f),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = task.priority,
                    color = priorityColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

