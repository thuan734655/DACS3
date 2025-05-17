package com.example.dacs3.ui.task

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Task
import com.example.dacs3.viewmodel.MyTaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTaskScreen(
    viewModel: MyTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTaskSelected: (String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // State for filter dialog
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // For bottom nav
    val selectedItem = 0 // Home is selected
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Công việc của tôi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // Filter button
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Lọc")
                    }
                    
                    // Refresh button
                    IconButton(onClick = { viewModel.loadMyTasks() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
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
            } else if (uiState.filteredTasks.isEmpty()) {
                EmptyTasksMessage(
                    onRefresh = { viewModel.loadMyTasks() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show applied filters if any
                    if (uiState.selectedStatusFilter != null || uiState.selectedPriorityFilter != null) {
                        item {
                            ActiveFiltersChips(
                                statusFilter = uiState.selectedStatusFilter,
                                priorityFilter = uiState.selectedPriorityFilter,
                                onClearFilters = { viewModel.resetFilters() }
                            )
                        }
                    }
                    
                    // Tasks list
                    items(uiState.filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskSelected(task._id) }
                        )
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
            
            // Filter dialog
            if (showFilterDialog) {
                FilterDialog(
                    currentStatus = uiState.selectedStatusFilter,
                    currentPriority = uiState.selectedPriorityFilter,
                    onDismiss = { showFilterDialog = false },
                    onApplyFilters = { status, priority ->
                        viewModel.applyFilters(status, priority)
                        showFilterDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyTasksMessage(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Không có công việc nào",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Hiện tại bạn không được giao công việc nào",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Làm mới")
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Task title and priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                PriorityChip(priority = task.priority)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description (if available)
            task.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status and due date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = task.status)
                
                task.due_date?.let { dueDate ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        Text(
                            text = dateFormat.format(dueDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
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
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
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
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ActiveFiltersChips(
    statusFilter: String?,
    priorityFilter: String?,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = "Bộ lọc đang áp dụng:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            statusFilter?.let {
                FilterChip(
                    label = "Trạng thái: $it",
                    onClick = onClearFilters
                )
            }
            
            priorityFilter?.let {
                FilterChip(
                    label = "Ưu tiên: $it",
                    onClick = onClearFilters
                )
            }
            
            IconButton(
                onClick = onClearFilters,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Xóa lọc",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun FilterChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentStatus: String?,
    currentPriority: String?,
    onDismiss: () -> Unit,
    onApplyFilters: (status: String?, priority: String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var selectedPriority by remember { mutableStateOf(currentPriority) }
    
    val statusOptions = listOf("Todo", "In Progress", "Review", "Done")
    val priorityOptions = listOf("Low", "Medium", "High")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lọc công việc") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Trạng thái",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status options
                statusOptions.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = if (selectedStatus == status) null else status }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = if (selectedStatus == status) null else status }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(text = status)
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "Mức độ ưu tiên",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority options
                priorityOptions.forEach { priority ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPriority = if (selectedPriority == priority) null else priority }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = if (selectedPriority == priority) null else priority }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(text = priority)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApplyFilters(selectedStatus, selectedPriority) }
            ) {
                Text("Áp dụng")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Hủy")
            }
        }
    )
}
