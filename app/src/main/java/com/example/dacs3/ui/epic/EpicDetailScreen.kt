package com.example.dacs3.ui.epic

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
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpicDetailScreen(
    epicId: String,
    viewModel: EpicDetailViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit = {},
    onCreateTask: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val epicData by viewModel.epic.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    
    // Edit fields
    val editName by viewModel.editName.collectAsState()
    val editDescription by viewModel.editDescription.collectAsState()
    val editPriority by viewModel.editPriority.collectAsState()
    
    // Status selection
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = Status.values()
    
    // Load epic data when screen is created
    LaunchedEffect(epicId) {
        viewModel.setEpicId(epicId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Epic Details") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (isEditMode) {
                    // Save button
                    IconButton(onClick = { viewModel.saveChanges() }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Changes"
                        )
                    }
                    // Cancel button
                    IconButton(onClick = { viewModel.cancelEdit() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel Editing"
                        )
                    }
                } else {
                    // Edit button
                    IconButton(onClick = { viewModel.enterEditMode() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Epic"
                        )
                    }
                    IconButton(onClick = onNavigateToTasks) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "View Tasks"
                        )
                    }
                    IconButton(onClick = onCreateTask) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task"
                        )
                    }
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
        } else if (epicData != null) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Epic details card
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
                        epicData?.let { epic ->
                            if (isEditMode) {
                                // Edit mode UI
                                OutlinedTextField(
                                    value = editName,
                                    onValueChange = { viewModel.updateName(it) },
                                    label = { Text("Epic Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                OutlinedTextField(
                                    value = editDescription,
                                    onValueChange = { viewModel.updateDescription(it) },
                                    label = { Text("Description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 5
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "Priority: ${getPriorityText(editPriority)}",
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Slider(
                                    value = editPriority.toFloat(),
                                    onValueChange = { viewModel.updatePriority(it.toInt()) },
                                    valueRange = 1f..5f,
                                    steps = 3,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF6B4EFF),
                                        activeTrackColor = Color(0xFF6B4EFF),
                                        activeTickColor = Color(0xFF6B4EFF)
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Low", fontSize = 12.sp, color = Color.Gray)
                                    Text("Medium", fontSize = 12.sp, color = Color.Gray)
                                    Text("High", fontSize = 12.sp, color = Color.Gray)
                                }
                            } else {
                                // View mode UI
                                Text(
                                    text = epic.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = epic.description,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                
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
                                                    text = epic.status.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = when (epic.status) {
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
                                                            viewModel.updateEpicStatus(status)
                                                            statusExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    InfoItem(label = "Priority", value = getPriorityText(epic.priority))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tasks counter
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
                            text = "Tasks (${tasks.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val todoCount = tasks.count { it.status == Status.TO_DO }
                            val inProgressCount = tasks.count { it.status == Status.IN_PROGRESS }
                            val doneCount = tasks.count { it.status == Status.DONE }
                            
                            StatusCounter(
                                label = "To Do",
                                count = todoCount,
                                color = Color(0xFFE57373)
                            )
                            
                            StatusCounter(
                                label = "In Progress",
                                count = inProgressCount,
                                color = Color(0xFF64B5F6)
                            )
                            
                            StatusCounter(
                                label = "Done",
                                count = doneCount,
                                color = Color(0xFF81C784)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToTasks,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6B4EFF)
                                )
                            ) {
                                Text("View All Tasks")
                            }
                            
                            Button(
                                onClick = onCreateTask,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6B4EFF)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Task")
                            }
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
                    text = "Epic not found",
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

@Composable
private fun StatusCounter(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
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