package com.example.dacs3.ui.sprint

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintDetailScreen(
    sprintId: String,
    onNavigateBack: () -> Unit,
    viewModel: SprintViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    // Tìm sprint hiện tại từ danh sách
    val currentSprint = uiState.sprints.find { it._id == sprintId }
    val tasks = uiState.sprintTasks[sprintId] ?: emptyList()
    
    // Format date
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    LaunchedEffect(sprintId) {
        // Tải thông tin sprint và các task của sprint
        viewModel.loadSprintDetail(sprintId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentSprint?.name ?: "Chi tiết Sprint") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa")
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
                    text = "Không tìm thấy thông tin Sprint",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Thông tin Sprint
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
                                
                                // Trạng thái
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Trạng thái: ",
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    val statusColor = when(currentSprint.status) {
                                        "To Do" -> Color(0xFF9E9E9E)
                                        "In Progress" -> Color(0xFF2196F3)
                                        "Done" -> Color(0xFF4CAF50)
                                        else -> Color(0xFF9E9E9E)
                                    }
                                    
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = statusColor.copy(alpha = 0.1f),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text(
                                            text = currentSprint.status,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Mô tả
                                if (!currentSprint.description.isNullOrEmpty()) {
                                    Text(
                                        text = "Mô tả:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = currentSprint.description,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                // Mục tiêu
                                if (!currentSprint.goal.isNullOrEmpty()) {
                                    Text(
                                        text = "Mục tiêu:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = currentSprint.goal,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                // Thời gian
                                Text(
                                    text = "Thời gian:",
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Từ ${dateFormat.format(currentSprint.start_date)} đến ${dateFormat.format(currentSprint.end_date)}",
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Nút thay đổi trạng thái
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (currentSprint.status != "In Progress") {
                                        Button(
                                            onClick = { viewModel.updateSprintStatus(sprintId, "In Progress") },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2196F3)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Bắt đầu Sprint")
                                        }
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    
                                    if (currentSprint.status != "Done") {
                                        Button(
                                            onClick = { viewModel.updateSprintStatus(sprintId, "Done") },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Hoàn thành Sprint")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Tiêu đề danh sách task
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Danh sách công việc (${tasks.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Button(
                                onClick = { /* Điều hướng đến trang tạo task */ },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Thêm task"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Thêm task")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Danh sách task
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
                                        text = "Chưa có công việc nào trong Sprint này",
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
            
            // Hiển thị lỗi nếu có
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
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

