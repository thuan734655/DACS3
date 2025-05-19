//package com.example.dacs3.ui.task
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.constraintlayout.compose.ConstraintLayout
//import androidx.constraintlayout.compose.Dimension
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.dacs3.data.model.Task
//import com.example.dacs3.ui.task.TaskViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TaskScreen(
//    epicId: String? = null,
//    workspaceId: String? = null,
//    onNavigateBack: () -> Unit,
//    onTaskSelected: (Task) -> Unit,
//    onCreateTask: () -> Unit = {},
//    viewModel: TaskViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(epicId, workspaceId) {
//        when {
//            epicId != null -> viewModel.getTasksByEpicId(epicId)
//            workspaceId != null -> viewModel.getAllTasks(workspaceId)
//        }
//    }
//
//    ConstraintLayout(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5))
//    ) {
//        val (topBar, content, fab) = createRefs()
//
//        // Top Bar
//        TopAppBar(
//            title = { Text("Danh sách công việc") },
//            navigationIcon = {
//                IconButton(onClick = onNavigateBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = Color.White
//            ),
//            modifier = Modifier.constrainAs(topBar) {
//                top.linkTo(parent.top)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//                width = Dimension.fillToConstraints
//            }
//        )
//
//        // Content
//        Box(
//            modifier = Modifier.constrainAs(content) {
//                top.linkTo(topBar.bottom)
//                bottom.linkTo(parent.bottom)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//                width = Dimension.fillToConstraints
//                height = Dimension.fillToConstraints
//            }
//        ) {
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            } else if (uiState.error != null) {
//                Text(
//                    text = uiState.error ?: "Đã xảy ra lỗi",
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .padding(16.dp)
//                )
//            } else if (uiState.tasks.isEmpty()) {
//                Text(
//                    text = "Không có công việc nào",
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .padding(16.dp)
//                )
//            } else {
//                LazyColumn(
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(uiState.tasks) { task ->
//                        TaskItem(
//                            task = task,
//                            onClick = { onTaskSelected(task) }
//                        )
//                    }
//                }
//            }
//        }
//
//        // FAB
//        FloatingActionButton(
//            onClick = onCreateTask,
//            containerColor = MaterialTheme.colorScheme.primary,
//            contentColor = Color.White,
//            modifier = Modifier.constrainAs(fab) {
//                bottom.linkTo(parent.bottom, margin = 16.dp)
//                end.linkTo(parent.end, margin = 16.dp)
//            }
//        ) {
//            Icon(Icons.Default.Add, contentDescription = "Thêm công việc")
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TaskItem(
//    task: Task,
//    onClick: () -> Unit
//) {
//    Card(
//        onClick = onClick,
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 2.dp
//        ),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = task.title,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.weight(1f)
//                )
//
//                PriorityTag(priority = task.priority)
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            task.description?.let {
//                if (it.isNotEmpty()) {
//                    Text(
//                        text = it,
//                        style = MaterialTheme.typography.bodyMedium,
//                        maxLines = 2,
//                        modifier = Modifier.padding(top = 4.dp)
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                StatusTag(status = task.status)
//
//                Text(
//                    text = "Người thực hiện: ${task.assigned_to ?: "Chưa gán"}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun PriorityTag(priority: String) {
//    val backgroundColor = when (priority.lowercase()) {
//        "low" -> Color(0xFFE3F2FD)
//        "medium" -> Color(0xFFFFF9C4)
//        "high" -> Color(0xFFFFCDD2)
//        else -> Color(0xFFEFEFEF)
//    }
//
//    val textColor = when (priority.lowercase()) {
//        "low" -> Color(0xFF1976D2)
//        "medium" -> Color(0xFFFBC02D)
//        "high" -> Color(0xFFD32F2F)
//        else -> Color.Gray
//    }
//
//    Surface(
//        shape = RoundedCornerShape(4.dp),
//        color = backgroundColor,
//        modifier = Modifier.padding(4.dp)
//    ) {
//        Text(
//            text = priority,
//            style = MaterialTheme.typography.bodySmall,
//            color = textColor,
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//        )
//    }
//}
//
//@Composable
//fun StatusTag(status: String) {
//    val backgroundColor = when (status.lowercase()) {
//        "todo" -> Color(0xFFE0E0E0)
//        "in progress" -> Color(0xFFE1F5FE)
//        "done" -> Color(0xFFE8F5E9)
//        "review" -> Color(0xFFFFF3E0)
//        else -> Color(0xFFEFEFEF)
//    }
//
//    val textColor = when (status.lowercase()) {
//        "todo" -> Color.Gray
//        "in progress" -> Color(0xFF0288D1)
//        "done" -> Color(0xFF388E3C)
//        "review" -> Color(0xFFFF9800)
//        else -> Color.Gray
//    }
//
//    Surface(
//        shape = RoundedCornerShape(4.dp),
//        color = backgroundColor,
//        modifier = Modifier.padding(4.dp)
//    ) {
//        Text(
//            text = status,
//            style = MaterialTheme.typography.bodySmall,
//            color = textColor,
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//        )
//    }
//}