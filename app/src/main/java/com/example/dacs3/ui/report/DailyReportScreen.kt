package com.example.dacs3.ui.report

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Task
import com.example.dacs3.viewmodel.DailyReportData
import com.example.dacs3.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTaskSelected: (String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // For bottom nav
    val selectedItem = 0 // Home is selected
    
    // Date formatter
    val dateFormatter = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Báo cáo hàng ngày") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == 0,
                    onClick = { onNavigateToHome() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
                    label = { Text("Messages") },
                    selected = selectedItem == 1,
                    onClick = { onNavigateToMessages() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == 2,
                    onClick = { onNavigateToProfile() }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date selector
                    item {
                        DateSelector(
                            date = uiState.selectedDate,
                            onPrevDay = { viewModel.prevDay() },
                            onNextDay = { viewModel.nextDay() },
                            dateFormatter = dateFormatter
                        )
                    }
                    
                    // Summary cards
                    item {
                        SummaryCardRow(report = uiState.dailyReport)
                    }
                    
                    // Progress charts
                    item {
                        ProgressCard(report = uiState.dailyReport)
                    }
                    
                    // Workspace distribution
                    item {
                        WorkspaceDistributionCard(
                            tasksByWorkspace = uiState.dailyReport.tasksByWorkspace,
                            workspaceNames = uiState.workspaceNames
                        )
                    }
                    
                    // Priority distribution
                    item {
                        PriorityDistributionCard(
                            tasksByPriority = uiState.dailyReport.tasksByPriority
                        )
                    }
                    
                    // Task list header
                    item {
                        Text(
                            text = "Danh sách công việc (${uiState.allTasks.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Tasks for this day
                    if (uiState.allTasks.isEmpty()) {
                        item {
                            EmptyTasksMessage()
                        }
                    } else {
                        items(uiState.allTasks) { task ->
                            TaskCard(
                                task = task,
                                workspaceName = uiState.workspaceNames[task.workspace_id] ?: "Unknown",
                                onClick = { onTaskSelected(task._id) }
                            )
                        }
                    }
                }
            }
            
            // Error snackbar
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
fun DateSelector(
    date: Date,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    dateFormatter: SimpleDateFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevDay) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day")
        }
        
        Text(
            text = dateFormatter.format(date),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        val today = Calendar.getInstance()
        val selectedDate = Calendar.getInstance().apply { time = date }
        
        val isToday = today.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)
        
        IconButton(
            onClick = onNextDay,
            enabled = !isToday
        ) {
            Icon(
                Icons.Default.ChevronRight, 
                contentDescription = "Next Day",
                tint = if (isToday) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else LocalContentColor.current
            )
        }
    }
}

@Composable
fun SummaryCardRow(report: DailyReportData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            title = "Tổng số",
            value = "${report.totalTasks}",
            icon = Icons.Default.Assignment,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        
        SummaryCard(
            title = "Hoàn thành",
            value = "${report.completedTasks}",
            icon = Icons.Default.Done,
            color = Color.Green,
            modifier = Modifier.weight(1f)
        )
        
        SummaryCard(
            title = "Đang làm",
            value = "${report.inProgressTasks}",
            icon = Icons.Default.Pending,
            color = Color.Blue,
            modifier = Modifier.weight(1f)
        )
        
        SummaryCard(
            title = "Mới",
            value = "${report.newTasks}",
            icon = Icons.Default.Add,
            color = Color.Magenta,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProgressCard(report: DailyReportData) {
    val completionPercentage = if (report.totalTasks > 0) {
        (report.completedTasks.toFloat() / report.totalTasks.toFloat()) * 100f
    } else {
        0f
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tiến độ công việc",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${completionPercentage.toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = completionPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressLegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Total: ${report.totalTasks}"
                )
                
                ProgressLegendItem(
                    color = Color.Green,
                    label = "Done: ${report.completedTasks}"
                )
                
                ProgressLegendItem(
                    color = Color.Blue,
                    label = "In Progress: ${report.inProgressTasks}"
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Tổng số giờ: ${report.totalHoursSpent} giờ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProgressLegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = CircleShape)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun WorkspaceDistributionCard(
    tasksByWorkspace: Map<String, Int>,
    workspaceNames: Map<String, String>
) {
    if (tasksByWorkspace.isEmpty()) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Phân bố theo không gian làm việc",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val totalTasks = tasksByWorkspace.values.sum()
                
                tasksByWorkspace.forEach { (workspaceId, count) ->
                    val workspaceName = workspaceNames[workspaceId] ?: "Unknown"
                    val percentage = (count.toFloat() / totalTasks.toFloat()) * 100f
                    
                    DistributionBar(
                        label = workspaceName,
                        value = "$count tasks",
                        percentage = percentage
                    )
                }
            }
        }
    }
}

@Composable
fun PriorityDistributionCard(
    tasksByPriority: Map<String, Int>
) {
    if (tasksByPriority.isEmpty()) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Phân bố theo ưu tiên",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val totalTasks = tasksByPriority.values.sum()
                
                tasksByPriority.forEach { (priority, count) ->
                    val percentage = (count.toFloat() / totalTasks.toFloat()) * 100f
                    val color = when (priority.lowercase()) {
                        "high" -> Color.Red
                        "medium" -> Color.Yellow
                        "low" -> Color.Green
                        else -> MaterialTheme.colorScheme.primary
                    }
                    
                    DistributionBar(
                        label = priority,
                        value = "$count tasks",
                        percentage = percentage,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun DistributionBar(
    label: String,
    value: String,
    percentage: Float,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(8.dp)
                    .background(color)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    workspaceName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                StatusChip(status = task.status)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Workspace: $workspaceName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${task.spent_hours}h spent",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                PriorityChip(priority = task.priority)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, contentColor) = when (status.lowercase()) {
        "todo" -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        "in progress" -> Pair(Color.Blue.copy(alpha = 0.2f), Color.Blue)
        "review" -> Pair(Color.Yellow.copy(alpha = 0.2f), Color.DarkGray)
        "done" -> Pair(Color.Green.copy(alpha = 0.2f), Color.Green.copy(alpha = 0.8f))
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityChip(priority: String) {
    val (backgroundColor, contentColor) = when (priority.lowercase()) {
        "high" -> Pair(Color.Red.copy(alpha = 0.2f), Color.Red)
        "medium" -> Pair(Color.Yellow.copy(alpha = 0.2f), Color.DarkGray)
        "low" -> Pair(Color.Green.copy(alpha = 0.2f), Color.DarkGray)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyTasksMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Today,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Không có công việc nào trong ngày này",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
