package com.example.dacs3.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dacs3.data.model.Comment
import com.example.dacs3.ui.task.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onEditTask: (String) -> Unit,
    viewModel: TaskViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel() // Thêm dòng này
) {
    val uiState by viewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()
    val task = uiState.selectedTask

    LaunchedEffect(taskId) {
        viewModel.getTaskById(taskId)
        // Giả sử task có workspace_id
        task?.workspace_id?.let { userViewModel.loadWorkspaceMembers(it) }
    }
    
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        val (topBar, content) = createRefs()
        
        // Top Bar
        TopAppBar(
            title = { Text("Chi tiết công việc") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                }
            },
            actions = {
                IconButton(onClick = { onEditTask(taskId) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
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
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Đã xảy ra lỗi",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (task == null) {
                Text(
                    text = "Không tìm thấy thông tin công việc",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
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
                                // Header with title and priority
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    PriorityTag(priority = task.priority)
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Status
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Trạng thái:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    StatusTag(status = task.status)
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Description
                                Text(
                                    text = "Mô tả:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = task.description ?: "Không có mô tả",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Assignee
                                Text(
                                    text = "Người thực hiện:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                val assignee = userState.workspaceMembers.find { member -> member._id == task.assigned_to }
                                if (assignee != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = assignee.avatar,
                                            contentDescription = "Avatar",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                        )
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Text(
                                            text = assignee.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Chưa phân công",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Created by
                                Text(
                                    text = "Người tạo:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                val currentUser = userState.currentUser
                                val creator = userState.workspaceMembers.find { member -> member._id == task.created_by }
                                if (creator != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = creator.avatar,
                                            contentDescription = "Avatar",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                        )
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Text(
                                            text = creator.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Không rõ người tạo",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Dates
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                
                                Text(
                                    text = "Ngày tạo: ${dateFormat.format(task.created_at)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                
                                task.updated_at?.let {
                                    Text(
                                        text = "Cập nhật lần cuối: ${dateFormat.format(it)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                    
                    // Comments section
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Bình luận (${task.comments?.size ?: 0})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (task.comments.isNullOrEmpty()) {
                        item {
                            Text(
                                text = "Chưa có bình luận nào",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        items(task.comments) { comment ->
                            CommentItem(comment = comment)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }



@Composable
fun SomeFunction() {
    // Gọi các hàm @Composable ở đây
}

@Composable
fun CommentItem(comment: Comment) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = comment.user?.avatar,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = comment.user?.name ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                Text(
                    text = dateFormat.format(comment.created_at),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}