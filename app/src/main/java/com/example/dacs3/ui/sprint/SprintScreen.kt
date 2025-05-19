package com.example.dacs3.ui.sprint

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.model.Task
import com.example.dacs3.data.model.UserInfo
import com.example.dacs3.util.WorkspacePreferences
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintScreen(
    viewModel: SprintViewModel = hiltViewModel(),
    workspaceId: String,
    onNavigateBack: () -> Unit,
    onSprintSelected: (Sprint) -> Unit,
    onCreateSprint: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Lấy WorkspacePreferences thông qua context
    val workspacePreferencesObject = remember { 
        com.example.dacs3.util.WorkspacePreferences(context)
    }

    // Sử dụng workspaceId từ WorkspacePreferences hoặc từ tham số được truyền vào
    val selectedWorkspaceId = remember { mutableStateOf(workspaceId) }
    
    // Kiểm tra workspaceId mới từ WorkspacePreferences
    LaunchedEffect(Unit) {
        val savedWorkspaceId = workspacePreferencesObject.getSelectedWorkspaceId()
        android.util.Log.d("SprintScreen", "Loaded workspaceId from preferences: $savedWorkspaceId")
        if (savedWorkspaceId.isNotEmpty()) {
            selectedWorkspaceId.value = savedWorkspaceId
        }
    }
    
    // Cập nhật SprintViewModel với workspaceId mới nhất
    LaunchedEffect(selectedWorkspaceId.value) {
        android.util.Log.d("SprintScreen", "Setting workspaceId in ViewModel: ${selectedWorkspaceId.value}")
        viewModel.setWorkspaceId(selectedWorkspaceId.value)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sprint") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            val (createSprintCard, sprintList) = createRefs()
            
            // Card tạo sprint mới
            Card(
                modifier = Modifier
                    .constrainAs(createSprintCard) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCreateSprint(workspaceId) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Create Sprint",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF6200EE), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create New Sprint",
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Danh sách sprint
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .constrainAs(sprintList) {
                            top.linkTo(createSprintCard.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.sprints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .constrainAs(sprintList) {
                            top.linkTo(createSprintCard.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("No sprints available")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .constrainAs(sprintList) {
                            top.linkTo(createSprintCard.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.sprints) { sprint ->
                        val isExpanded = uiState.expandedSprintIds.contains(sprint._id)
                        val tasks = uiState.sprintTasks[sprint._id] ?: emptyList()
                        
                        SprintItem(
                            sprint = sprint,
                            tasks = tasks,
                            isExpanded = isExpanded,
                            onSprintClick = { 
                                // Chỉ toggle khi nhấp vào nút mở rộng, các tương tác khác dẫn đến xem chi tiết
                                viewModel.toggleSprintExpansion(sprint._id) 
                            },
                            onSprintSelected = { 
                                // Điều hướng đến trang chi tiết Sprint khi nhấp vào Sprint
                                onSprintSelected(sprint) 
                            },
                            onCompleteClick = { 
                                viewModel.updateSprintStatus(sprint._id, "Done") 
                            },
                            onStartClick = { 
                                viewModel.updateSprintStatus(sprint._id, "In Progress") 
                            },
                            onSeeMoreClick = { onSprintSelected(sprint) }
                        )
                    }
                }
            }
            
            // Hiển thị lỗi nếu có
            if (uiState.error != null) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(uiState.error!!)
                }
            }
        }
    }
}

@Composable
fun SprintItem(
    sprint: Sprint,
    tasks: List<Task>,
    isExpanded: Boolean,
    onSprintClick: () -> Unit,
    onSprintSelected: () -> Unit,
    onCompleteClick: () -> Unit,
    onStartClick: () -> Unit,
    onSeeMoreClick: () -> Unit
) {
    // Format dates for display
    val startDate = SimpleDateFormat("dd MMM", Locale.getDefault()).format(sprint.start_date)
    val endDate = SimpleDateFormat("dd MMM", Locale.getDefault()).format(sprint.end_date)
    val dateRange = "$startDate - $endDate (${tasks.size} work items)"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSprintSelected() }, // Toàn bộ card có thể nhấn để xem chi tiết
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (title, date, menu, expandIcon, taskList, actionButton, seeMore) = createRefs()
            
            // Tiêu đề sprint
            Text(
                text = sprint.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            )
            
            // Ngày và số lượng công việc
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.constrainAs(date) {
                    top.linkTo(title.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
            )
            
            // Menu điểm ba
            IconButton(
                onClick = { onSprintSelected() }, // Thêm hành động xem chi tiết Sprint khi nhấn vào menu
                modifier = Modifier.constrainAs(menu) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.Gray
                )
            }
            
            // Icon mở rộng/thu gọn
            IconButton(
                onClick = onSprintClick,
                modifier = Modifier.constrainAs(expandIcon) {
                    top.linkTo(parent.top)
                    end.linkTo(menu.start)
                }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "Thu gọn" else "Mở rộng",
                    tint = Color.Gray
                )
            }
            
            // Danh sách task (chỉ hiển thị khi mở rộng)
            if (isExpanded) {
                Column(
                    modifier = Modifier.constrainAs(taskList) {
                        top.linkTo(date.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    // Hiển thị tối đa 3 task
                    val displayTasks = if (tasks.size > 3) tasks.take(3) else tasks
                    
                    displayTasks.forEach { task ->
                        TaskItem(task = task)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                }
            }

            // Nút "See More" nếu có nhiều hơn 3 task
            if (tasks.size > 3) {
                Row(
                    modifier = Modifier
                        .constrainAs(seeMore) {
                            top.linkTo(taskList.bottom, margin = 8.dp)
                            end.linkTo(parent.end)
                        }
                        .clickable { onSeeMoreClick() },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("See More", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }

            // Action buttons (Start/Complete Sprint and Details)
            Row(
                modifier = Modifier.constrainAs(actionButton) {
                    top.linkTo(if (isExpanded && tasks.isNotEmpty()) {
                        if (tasks.size > 3) seeMore.bottom else taskList.bottom
                    } else date.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    width = Dimension.fillToConstraints
                },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sprint action button (Start/Complete)
                Button(
                    onClick = { 
                        if (sprint.status == "Done") {
                            // Already completed, nothing to do
                        } else if (sprint.status == "In Progress") {
                            onCompleteClick()
                        } else {
                            onStartClick()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (sprint.status) {
                            "Done" -> Color.Gray
                            "In Progress" -> Color(0xFF4CAF50)
                            else -> Color(0xFF2196F3)
                        }
                    )
                ) {
                    Text(
                        text = when (sprint.status) {
                            "Done" -> "Completed"
                            "In Progress" -> "Complete Sprint"
                            else -> "Start Sprint"
                        }
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Details button
                Button(
                    onClick = { onSprintSelected() },
                    modifier = Modifier.weight(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Details")
                }
            }
        }
    }
}
@Composable
fun TaskItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = task.description ?: "Brainstorming brings team members' diverse ...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Priority tag
        val priorityColor = when(task.priority.lowercase()) {
            "high" -> Color(0xFFF44336)
            "medium" -> Color(0xFFFF9800)
            else -> Color(0xFF4CAF50)
        }
        
        val priorityBgColor = when(task.priority.lowercase()) {
            "high" -> Color(0xFFFFEBEE)
            "medium" -> Color(0xFFFFF3E0)
            else -> Color(0xFFE8F5E9)
        }
        
        Box(
            modifier = Modifier
                .background(priorityBgColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = task.priority,
                style = MaterialTheme.typography.bodySmall,
                color = priorityColor
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Edit icon
        IconButton(
            onClick = { /* Edit task */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Chỉnh sửa",
                tint = Color.Gray
            )
        }
        
        // Assignees (hiển thị avatar của người được giao)
        Row(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            // Hiển thị avatar người dùng thực tế nếu có
            // Giả lập 3 avatar người dùng
            for (i in 0 until 3) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9C27B0))
                )
            }
        }
    }
}
