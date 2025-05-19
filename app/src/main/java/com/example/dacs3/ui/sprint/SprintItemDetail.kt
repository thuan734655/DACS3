package com.example.dacs3.ui.sprint

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SprintItemDetail(  // Renamed from SprintItem to SprintItemDetail
    sprint: Sprint,
    tasks: List<Task>,
    isExpanded: Boolean,
    onSprintClick: () -> Unit,
    onSprintSelected: () -> Unit,
    onCompleteClick: () -> Unit,
    onStartClick: () -> Unit,
    onSeeMoreClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sprint header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSprintClick)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = sprint.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "From ${dateFormat.format(sprint.start_date)} to ${dateFormat.format(sprint.end_date)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Status tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusColor = when(sprint.status) {
                            "To Do" -> Color(0xFF9E9E9E)
                            "In Progress" -> Color(0xFF2196F3)
                            "Done" -> Color(0xFF4CAF50)
                            else -> Color(0xFF9E9E9E)
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(statusColor, CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = sprint.status,
                            fontSize = 12.sp,
                            color = statusColor
                        )
                    }
                }
                
                // Expand/collapse icon
                IconButton(
                    onClick = onSprintClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationState)
                    )
                }
            }
            
            // Expanded content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Sprint description
                    if (!sprint.description.isNullOrEmpty()) {
                        Text(
                            text = sprint.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Sprint goal
                    if (!sprint.goal.isNullOrEmpty()) {
                        Text(
                            text = "Goal: ${sprint.goal}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Tasks
                    Text(
                        text = "Tasks (${tasks.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (tasks.isEmpty()) {
                        Text(
                            text = "No tasks available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        // Show first 3 tasks
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tasks.take(3).forEach { task ->
                                TaskItemInSprintList(task = task)
                            }
                            
                            // See more button if there are more than 3 tasks
                            if (tasks.size > 3) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = onSeeMoreClick)
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "See more",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "See more",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onSprintSelected,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Details")
                        }
                        
                        when (sprint.status) {
                            "To Do" -> {
                                Button(
                                    onClick = onStartClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2196F3)
                                    )
                                ) {
                                    Text("Start Sprint")
                                }
                            }
                            "In Progress" -> {
                                Button(
                                    onClick = onCompleteClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Text("Complete Sprint")
                                }
                            }
                            "Done" -> {
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Gray
                                    )
                                ) {
                                    Text("Completed")
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
fun TaskItemInSprintList(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status indicator
        val statusColor = when(task.status) {
            "To Do" -> Color(0xFF9E9E9E)
            "In Progress" -> Color(0xFF2196F3)
            "Done" -> Color(0xFF4CAF50)
            else -> Color(0xFF9E9E9E)
        }
        
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(statusColor, CircleShape)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Task title
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        // Priority indicator
        val priorityColor = when(task.priority.lowercase()) {
            "high" -> Color(0xFFE53935)
            "medium" -> Color(0xFFFFA000)
            "low" -> Color(0xFF43A047)
            else -> Color(0xFF9E9E9E)
        }
        
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(priorityColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = task.priority.first().toString(),
                fontSize = 12.sp,
                color = priorityColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}