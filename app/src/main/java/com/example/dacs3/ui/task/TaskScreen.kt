package com.example.dacs3.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.dacs3.data.model.Task
import com.example.dacs3.ui.components.BottomNavigationBar
import com.example.dacs3.ui.components.LoadingIndicator
import com.example.dacs3.ui.components.TaskPriorityBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    workspaceId: String? = null,
    viewModel: TaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTaskSelected: (String) -> Unit,
    onCreateTask: () -> Unit,
    onHomeClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Trigger task loading when the screen is shown
    LaunchedEffect(workspaceId) {
        android.util.Log.d("TaskScreenDebug", "TaskScreen launched with workspaceId: $workspaceId")
        workspaceId?.let {
            android.util.Log.d("TaskScreenDebug", "Loading tasks for workspaceId: $it")
            viewModel.loadTasksByWorkspaceId(it)
        } ?: android.util.Log.e("TaskScreenDebug", "workspaceId is null, cannot load tasks")
    }

    // UI state
    val uiState by viewModel.uiState.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        val (topBar, content, fab, bottomNav) = createRefs()

        // TopAppBar
        TopAppBar(
            title = { Text("Tasks") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
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
                bottom.linkTo(bottomNav.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        ) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "An error occurred",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.tasks) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onTaskSelected(task._id) }
                        )
                    }
                }
            }
        }

        // FAB for creating new task
        FloatingActionButton(
            onClick = onCreateTask,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier.constrainAs(fab) {
                bottom.linkTo(bottomNav.top, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Task")
        }

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = "tasks",
            onHomeClick = onHomeClick,
            onDashboardClick = onDashboardClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.constrainAs(bottomNav) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (priorityBadge, title, description, assignees) = createRefs()
            
            // Priority badge (Low, Medium, High)
            TaskPriorityBadge(
                priority = task.priority,
                modifier = Modifier.constrainAs(priorityBadge) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            )

            // Task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(priorityBadge.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Task description
            if (!task.description.isNullOrEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(description) {
                        top.linkTo(title.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            // Assignees (shown as avatar circles)
            if (task.assigned_to != null) {
                Row(
                    modifier = Modifier.constrainAs(assignees) {
                        top.linkTo(if (!task.description.isNullOrEmpty()) description.bottom else title.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "U",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
