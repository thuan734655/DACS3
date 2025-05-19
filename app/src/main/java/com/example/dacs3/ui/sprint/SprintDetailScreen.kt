package com.example.dacs3.ui.sprint

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintDetailScreen(
    sprintId: String,
    onNavigateBack: () -> Unit,
    onTaskSelected: (Task) -> Unit,
    viewModel: SprintViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val sprintUiState by viewModel.uiState.collectAsState()
    val taskUiState by taskViewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    
    // Lấy thông tin sprint và các task trong sprint
    LaunchedEffect(sprintId) {
        viewModel.getSprintById(sprintId)
        taskViewModel.getTasksBySprintId(sprintId)
    }
    
    val sprint = sprintUiState.selectedSprint
    
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        val (topBar, content) = createRefs()
        
        // Top Bar
        TopAppBar(
            title = { Text(sprint?.name ?: "Chi tiết Sprint") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                }
            },
            actions = {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Tùy chọn")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Chỉnh sửa Sprint") },
                        onClick = {
                            // Xử lý chỉnh sửa sprint
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Xóa Sprint") },
                        onClick = {
                            // Xử lý xóa sprint
                            showMenu = false
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            modifier = Modifier.constrainAs(topBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        
        // Content
        Box(
            modifier = Modifier.constrainAs(content) {
                top.linkTo(topBar.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            if (sprintUiState.isLoading || taskUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (sprint == null) {
                Text(
                    text = "Không tìm thấy thông tin Sprint",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Sprint Info Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Sprint name and dates
                            Text(
                                text = sprint.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val dateFormat = SimpleDateFormat("dd MMM - dd MMM", Locale.getDefault())
                            val dateRange = "${SimpleDateFormat("dd MMM", Locale.getDefault()).format(sprint.start_date)} - ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(sprint.end_date)}"
                            val taskCount = taskUiState.tasks.size
                            
                            Text(
                                text = "$dateRange ($taskCount công việc)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Sprint goal
                            sprint.goal?.let {
                                if (it.isNotEmpty()) {
                                    Text(
                                        text = "Mục tiêu: $it",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            
                            // Sprint status
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Trạng thái: ",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                SprintStatusChip(status = sprint.status)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tasks section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Danh sách công việc",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Button(
                            onClick = { /* Xử lý hoàn thành sprint */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text("Hoàn thành Sprint")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (taskUiState.tasks.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không có công việc nào trong sprint này",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(taskUiState.tasks) { task ->
                                TaskItemInSprint(
                                    task = task,
                                    onClick = { onTaskSelected(task) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Error messages
            sprintUiState.error?.let { error ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItemInSprint(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Brainstorming brings team members' diverse ...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Priority tag
            Box(
                modifier = Modifier
                    .background(
                        color = when (task.priority.lowercase()) {
                            "low" -> Color(0xFFFFF9C4)
                            "medium" -> Color(0xFFFFE0B2)
                            "high" -> Color(0xFFFFCCBC)
                            "urgent" -> Color(0xFFFFCDD2)
                            else -> Color(0xFFE0E0E0)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Low",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Assignee avatars
            Row {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBBDEFB))
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1C4E9))
                )
            }
        }
    }
}

@Composable
fun SprintStatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "to do" -> Pair(Color(0xFFE3F2FD), Color(0xFF1976D2))
        "in progress" -> Pair(Color(0xFFFFF8E1), Color(0xFFFFA000))
        "done" -> Pair(Color(0xFFE8F5E9), Color(0xFF388E3C))
        else -> Pair(Color(0xFFEEEEEE), Color.DarkGray)
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TaskViewModel() {
    // Placeholder for TaskViewModel
    // Trong triển khai thực tế, bạn sẽ tạo một ViewModel riêng cho Task
}