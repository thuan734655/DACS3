package com.example.dacs3.ui.bugs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.local.BugEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugListScreen(
    taskId: String,
    viewModel: BugListViewModel = hiltViewModel(),
    onBugClick: (String) -> Unit = {},
    onCreateBug: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val bugs by viewModel.bugs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val task by viewModel.task.collectAsState()
    
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
            title = { Text(task?.name ?: "Bugs") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = onCreateBug) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Bug"
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
        } else if (bugs.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No bugs reported yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onCreateBug,
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
        } else {
            // Bug list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bugs) { bug ->
                    BugItem(
                        bug = bug,
                        onClick = { onBugClick(bug.bugId) }
                    )
                }
                
                // Add some padding at the bottom
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun BugItem(
    bug: BugEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bug icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFE57373).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Bug info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = bug.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (bug.description.isNotBlank()) {
                        Text(
                            text = bug.description,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
                
                // Priority badge
                PriorityBadge(priority = bug.priority)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = bug.status)
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: Int) {
    val (backgroundColor, textColor) = when (priority) {
        1 -> Color(0xFFBBDEFB) to Color(0xFF1976D2)  // Low
        2 -> Color(0xFFD7CCC8) to Color(0xFF5D4037)  // Medium-Low
        3 -> Color(0xFFFFCC80) to Color(0xFFEF6C00)  // Medium
        4 -> Color(0xFFFFAB91) to Color(0xFFD84315)  // Medium-High
        5 -> Color(0xFFEF9A9A) to Color(0xFFB71C1C)  // High
        else -> Color(0xFFFFCC80) to Color(0xFFEF6C00)  // Default Medium
    }
    
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = when (priority) {
                1 -> "Very Low"
                2 -> "Low"
                3 -> "Medium"
                4 -> "High"
                5 -> "Critical"
                else -> "Medium"
            },
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatusBadge(status: com.example.dacs3.data.local.Status) {
    val (backgroundColor, textColor) = when (status) {
        com.example.dacs3.data.local.Status.TO_DO -> Color(0xFFE57373) to Color.White
        com.example.dacs3.data.local.Status.IN_PROGRESS -> Color(0xFF64B5F6) to Color.White
        com.example.dacs3.data.local.Status.DONE -> Color(0xFF81C784) to Color.White
    }
    
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
} 