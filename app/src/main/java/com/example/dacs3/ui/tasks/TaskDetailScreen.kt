package com.example.dacs3.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.local.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    viewModel: TaskDetailViewModel = hiltViewModel(),
    onNavigateToBugs: () -> Unit = {},
    onCreateBug: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val task by viewModel.task.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val bugs by viewModel.bugs.collectAsState()
    val assignedUser by viewModel.assignedUser.collectAsState()
    val createdByUser by viewModel.createdByUser.collectAsState()
    
    // Status selection
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = Status.values()
    
    // Load task data when screen is created
    LaunchedEffect(taskId) {
        viewModel.setTaskId(taskId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Task Details") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = onNavigateToBugs) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "View Bugs"
                    )
                }
                IconButton(onClick = onCreateBug) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Bug"
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
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6B4EFF))
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (task != null) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Task details card
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
                        task?.let { task ->
                            Text(
                                text = task.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (task.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = task.description,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Status with dropdown
                                Column {
                                    Text(
                                        text = "Status",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    
                                    Box {
                                        TextButton(
                                            onClick = { statusExpanded = true },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = task.status.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = when (task.status) {
                                                    Status.TO_DO -> Color(0xFFE57373)
                                                    Status.IN_PROGRESS -> Color(0xFF64B5F6)
                                                    Status.DONE -> Color(0xFF81C784)
                                                }
                                            )
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = "Change status"
                                            )
                                        }
                                        
                                        DropdownMenu(
                                            expanded = statusExpanded,
                                            onDismissRequest = { statusExpanded = false }
                                        ) {
                                            statusOptions.forEach { status ->
                                                DropdownMenuItem(
                                                    text = { Text(status.name) },
                                                    onClick = {
                                                        viewModel.updateTaskStatus(status)
                                                        statusExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                InfoItem(label = "Priority", value = getPriorityText(task.priority))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress card
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
                        task?.let { task ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progress",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Text(
                                    text = "${task.progress}%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B4EFF)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            LinearProgressIndicator(
                                progress = { task.progress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = Color(0xFF6B4EFF),
                                trackColor = Color(0xFFE0E0E0)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Progress adjustment
                            Slider(
                                value = task.progress.toFloat(),
                                onValueChange = { newValue ->
                                    viewModel.updateTaskProgress(newValue.toInt())
                                },
                                valueRange = 0f..100f,
                                steps = 19, // 100/5 = 20 steps (0, 5, 10, ..., 100)
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF6B4EFF),
                                    activeTrackColor = Color(0xFF6B4EFF)
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Assignment and creation info
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
                            text = "People",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Assigned to
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentInd,
                                contentDescription = null,
                                tint = Color(0xFF6B4EFF),
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "Assigned to:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = assignedUser?.username ?: "Not assigned",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Created by
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF6B4EFF),
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "Created by:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = createdByUser?.username ?: "Unknown",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bugs card
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bugs (${bugs.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            TextButton(
                                onClick = onNavigateToBugs,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFF6B4EFF)
                                )
                            ) {
                                Text("View All")
                            }
                        }
                        
                        if (bugs.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "No bugs reported for this task",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Show first 3 bugs
                            bugs.take(3).forEach { bug ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = when (bug.priority) {
                                            1 -> Color(0xFF81C784) // Low
                                            2, 3 -> Color(0xFFFFD54F) // Medium
                                            4, 5 -> Color(0xFFE57373) // High
                                            else -> Color.Gray
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = bug.name,
                                        fontSize = 14.sp,
                                        maxLines = 1
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = onCreateBug,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B4EFF)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Report Bug")
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Task not found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper function to get priority text
private fun getPriorityText(priority: Int): String {
    return when (priority) {
        1 -> "Very Low"
        2 -> "Low"
        3 -> "Medium"
        4 -> "High"
        5 -> "Critical"
        else -> "Medium"
    }
} 