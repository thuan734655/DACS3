package com.example.dacs3.ui.bugs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBugScreen(
    taskId: String,
    viewModel: CreateBugViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onBugCreated: () -> Unit = {}
) {
    val bugName by viewModel.bugName.collectAsState()
    val bugDescription by viewModel.bugDescription.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val status by viewModel.status.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val task by viewModel.task.collectAsState()
    
    // Status selection
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = Status.values()
    
    // Success effect
    LaunchedEffect(Unit) {
        viewModel.isSuccess.collectLatest { isSuccess ->
            if (isSuccess) {
                // Reset the success flag
                viewModel.resetSuccess()
                // Navigate back
                onBugCreated()
            }
        }
    }
    
    // Set the task ID when the screen is created
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
            title = { Text("Report Bug") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6B4EFF),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6B4EFF))
            }
        } else {
            // Form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Task info
                task?.let { currentTask ->
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
                                text = "For Task",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Text(
                                text = currentTask.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Form fields
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
                        // Bug name
                        OutlinedTextField(
                            value = bugName,
                            onValueChange = { viewModel.updateBugName(it) },
                            label = { Text("Bug Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Bug description
                        OutlinedTextField(
                            value = bugDescription,
                            onValueChange = { viewModel.updateBugDescription(it) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Priority slider
                        Text(
                            text = "Priority: ${getPriorityText(priority)}",
                            fontWeight = FontWeight.Medium
                        )
                        
                        Slider(
                            value = priority.toFloat(),
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
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Status dropdown
                        Column {
                            Text(
                                text = "Status",
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Box {
                                OutlinedTextField(
                                    value = status.name,
                                    onValueChange = { },
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Select Status"
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { statusExpanded = true }
                                )
                                
                                DropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    statusOptions.forEach { statusOption ->
                                        DropdownMenuItem(
                                            text = { Text(statusOption.name) },
                                            onClick = {
                                                viewModel.updateStatus(statusOption)
                                                statusExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Error message
                        if (error != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = error ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Submit button
                Button(
                    onClick = { viewModel.createBug() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B4EFF)
                    )
                ) {
                    Text("Report Bug")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
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