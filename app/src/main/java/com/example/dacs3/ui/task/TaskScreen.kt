package com.example.dacs3.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Epic
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.model.Task
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    epicId: String,
    viewModel: TaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTaskSelected: (Task) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(epicId) {
        viewModel.loadTasksByEpic(epicId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter Tasks")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.filteredTasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No tasks found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showCreateDialog = true }) {
                        Text("Create Task")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(uiState.filteredTasks) { task ->
                        var showDeleteDialog by remember { mutableStateOf(false) }
                        var showUpdateDialog by remember { mutableStateOf(false) }
                        
                        TaskItem(
                            task = task,
                            onClick = { onTaskSelected(task) },
                            onUpdateClick = {
                                // Open update dialog
                                showUpdateDialog = true
                            },
                            onDeleteClick = {
                                // Open delete confirmation dialog
                                showDeleteDialog = true
                            }
                        )
                        
                        if (showDeleteDialog) {
                            DeleteTaskDialog(
                                task = task,
                                onDismiss = { showDeleteDialog = false },
                                onConfirm = {
                                    viewModel.deleteTask(task._id)
                                    showDeleteDialog = false
                                }
                            )
                        }
                        
                        if (showUpdateDialog) {
                            UpdateTaskDialog(
                                task = task,
                                onDismiss = { showUpdateDialog = false },
                                onUpdateTask = { title, description, epicId, assignedTo, status, priority, estimatedHours, spentHours, startDate, dueDate, completedDate, sprintId ->
                                    viewModel.updateTask(
                                        id = task._id,
                                        title = title,
                                        description = description,
                                        epicId = epicId,
                                        assignedTo = assignedTo,
                                        status = status,
                                        priority = priority,
                                        estimatedHours = estimatedHours,
                                        spentHours = spentHours,
                                        startDate = startDate,
                                        dueDate = dueDate,
                                        completedDate = completedDate,
                                        sprintId = sprintId
                                    )
                                    showUpdateDialog = false
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            // Show error message if any
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
            
            // Create Task Dialog
            if (showCreateDialog) {
                // Get workspaceId from the current epicId
                val workspaceId = uiState.filteredTasks.firstOrNull()?.workspace_id ?: ""
                
                // Load epics for this workspace if not already loaded
                LaunchedEffect(workspaceId) {
                    if (workspaceId.isNotEmpty() && uiState.availableEpics.isEmpty()) {
                        viewModel.loadEpicsForWorkspace(workspaceId)
                    }
                }
                
                CreateTaskDialog(
                    availableEpics = uiState.availableEpics,
                    isLoadingEpics = uiState.isLoadingEpics,
                    currentEpicId = epicId,
                    onDismiss = { showCreateDialog = false },
                    onCreateTask = { title, description, epicId, assignedTo, status, priority, estimatedHours, sprintId, startDate, dueDate ->
                        viewModel.createTask(
                            title = title,
                            description = description,
                            workspaceId = workspaceId,
                            epicId = epicId,
                            assignedTo = assignedTo,
                            status = status,
                            priority = priority,
                            estimatedHours = estimatedHours,
                            sprintId = sprintId,
                            startDate = startDate,
                            dueDate = dueDate
                        )
                        showCreateDialog = false
                    },
                    viewModel = viewModel
                )
            }
            
            // Filter Dialog
            if (showFilterDialog) {
                FilterTaskDialog(
                    onDismiss = { showFilterDialog = false },
                    onApplyFilter = { status, priority ->
                        if (status != null) {
                            viewModel.filterTasksByStatus(status)
                        } else if (priority != null) {
                            viewModel.filterTasksByPriority(priority)
                        } else {
                            // Reset filters
                            viewModel.filterTasksByStatus(null)
                        }
                        showFilterDialog = false
                    }
                )
            }
            
            // Success messages
            if (uiState.isCreationSuccessful) {
                LaunchedEffect(uiState.isCreationSuccessful) {
                    // Reset the state after showing success
                    viewModel.resetCreationState()
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Task created successfully!")
                }
            }
            
            if (uiState.isUpdateSuccessful) {
                LaunchedEffect(uiState.isUpdateSuccessful) {
                    // Reset the state after showing success
                    viewModel.resetUpdateState()
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Task updated successfully!")
                }
            }
            
            if (uiState.isDeleteSuccessful) {
                LaunchedEffect(uiState.isDeleteSuccessful) {
                    // Reset the state after showing success
                    viewModel.resetDeleteState()
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Task deleted successfully!")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            task.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Priority Chip
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when(task.priority.lowercase()) {
                        "high" -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        "medium" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = task.priority,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = when(task.priority.lowercase()) {
                            "high" -> MaterialTheme.colorScheme.error
                            "medium" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }
                
                // Status Chip
                Surface(
                    modifier = Modifier.padding(start = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = when(task.status.lowercase()) {
                        "completed" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        "in_progress" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        "to_do" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = task.status.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = when(task.status.lowercase()) {
                            "completed" -> MaterialTheme.colorScheme.primary
                            "in_progress" -> MaterialTheme.colorScheme.tertiary
                            "to_do" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hours info
                Text(
                    text = "Estimated: ${task.estimated_hours}h / Spent: ${task.spent_hours}h",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Dates info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    task.start_date?.let {
                        Text(
                            text = "Start: ${dateFormat.format(it)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    task.due_date?.let {
                        Text(
                            text = "Due: ${dateFormat.format(it)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row {
                    // Edit button
                    IconButton(
                        onClick = onUpdateClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Delete button
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    // Assignee info if available
                    task.assigned_to?.let { assignee ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = assignee.take(2).uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    availableEpics: List<Epic>,
    isLoadingEpics: Boolean,
    currentEpicId: String,
    onDismiss: () -> Unit,
    onCreateTask: (title: String, description: String?, epicId: String, assignedTo: String?, status: String, priority: String, estimatedHours: Number, sprintId: String?, startDate: Date?, dueDate: Date?) -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("TO_DO") }
    var priority by remember { mutableStateOf("Medium") }
    var estimatedHours by remember { mutableStateOf("0") }
    var sprintId by remember { mutableStateOf<String?>(null) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var selectedEpicId by remember { mutableStateOf(currentEpicId) }
    
    // Get workspace ID from selected Epic to fetch users and sprints
    val workspaceId = availableEpics.find { it._id == currentEpicId }?.workspace_id?._id ?: ""
    
    // Load users and sprints for the workspace
    LaunchedEffect(workspaceId) {
        if (workspaceId.isNotEmpty()) {
            viewModel.loadWorkspaceUsers(workspaceId)
            viewModel.loadWorkspaceSprints(workspaceId)
        }
    }
    
    // UI state for users and sprints
    val uiState = viewModel.uiState.collectAsState().value
    val workspaceUsers = uiState.workspaceUsers
    val workspaceSprints = uiState.workspaceSprints
    
    // Date picker state
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Create Task",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Assigned To Dropdown with user avatars
                var expandedAssignedTo by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedAssignedTo,
                    onExpandedChange = { expandedAssignedTo = it }
                ) {
                    OutlinedTextField(
                        value = workspaceUsers.find { it._id == assignedTo }?.name ?: "Select User",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned To") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAssignedTo) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedAssignedTo,
                        onDismissRequest = { expandedAssignedTo = false }
                    ) {
                        if (uiState.isLoadingUsers) {
                            // Show loading indicator
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        } else if (workspaceUsers.isEmpty()) {
                            // Show no users message
                            DropdownMenuItem(
                                text = { Text("No users available") },
                                onClick = { expandedAssignedTo = false }
                            )
                        } else {
                            // Show all workspace users
                            workspaceUsers.forEach { user ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Avatar (can be customized further)
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = user.name.firstOrNull()?.toString() ?: "?",
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.width(8.dp))
                                            
                                            // Username
                                            Text(user.name)
                                        }
                                    },
                                    onClick = {
                                        assignedTo = user._id
                                        expandedAssignedTo = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status Dropdown
                var expandedStatus by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = it }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        listOf("TO_DO", "IN_PROGRESS", "DONE").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority Dropdown
                var expandedPriority by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = it }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedPriority,
                        onDismissRequest = { expandedPriority = false }
                    ) {
                        listOf("Low", "Medium", "High").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = estimatedHours,
                    onValueChange = { 
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            estimatedHours = it
                        }
                    },
                    label = { Text("Estimated Hours") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Sprint dropdown
                var expandedSprint by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedSprint,
                    onExpandedChange = { expandedSprint = it }
                ) {
                    OutlinedTextField(
                        value = workspaceSprints.find { it._id == sprintId }?.name ?: "Select Sprint",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sprint (Optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSprint) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedSprint,
                        onDismissRequest = { expandedSprint = false }
                    ) {
                        if (uiState.isLoadingSprints) {
                            // Show loading indicator
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        } else if (workspaceSprints.isEmpty()) {
                            // Show no sprints message
                            DropdownMenuItem(
                                text = { Text("No sprints available") },
                                onClick = { expandedSprint = false }
                            )
                        } else {
                            // Show all workspace sprints
                            workspaceSprints.forEach { sprint ->
                                DropdownMenuItem(
                                    text = { Text(sprint.name) },
                                    onClick = {
                                        sprintId = sprint._id
                                        expandedSprint = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Start Date
                OutlinedTextField(
                    value = startDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("Start Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Due Date
                OutlinedTextField(
                    value = dueDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("Due Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDueDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onCreateTask(
                                    title,
                                    description.ifBlank { null },
                                    selectedEpicId,
                                    assignedTo,
                                    status,
                                    priority,
                                    estimatedHours.toDoubleOrNull() ?: 0,
                                    sprintId,
                                    startDate,
                                    dueDate
                                )
                            }
                        },
                        enabled = title.isNotBlank() && selectedEpicId.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
    
    // Start Date Picker Dialog
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            startDate = Date(it)
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Due Date Picker Dialog
    if (showDueDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDueDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dueDate = Date(it)
                        }
                        showDueDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDueDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun FilterTaskDialog(
    onDismiss: () -> Unit,
    onApplyFilter: (status: String?, priority: String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 for Status, 1 for Priority
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Filter Tasks",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tab selection
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { 
                            selectedTab = 0
                            selectedPriority = null
                        },
                        text = { Text("By Status") }
                    )
                    
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { 
                            selectedTab = 1
                            selectedStatus = null
                        },
                        text = { Text("By Priority") }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (selectedTab) {
                    0 -> {
                        // Status filters
                        Column {
                            listOf(null, "TO_DO", "IN_PROGRESS", "DONE").forEach { status ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedStatus = status }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedStatus == status,
                                        onClick = { selectedStatus = status }
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = status ?: "All",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Priority filters
                        Column {
                            listOf(null, "Low", "Medium", "High").forEach { priority ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPriority = priority }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedPriority == priority,
                                        onClick = { selectedPriority = priority }
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = priority ?: "All",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { onApplyFilter(selectedStatus, selectedPriority) }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = { 
            Column {
                Text("Are you sure you want to delete this task?") 
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${task.title}\"", 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (title: String, description: String?, epicId: String?, assignedTo: String?, status: String?, priority: String?, estimatedHours: Number, spentHours: Number, startDate: Date?, dueDate: Date?, completedDate: Date?, sprintId: String?) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }
    var assignedTo by remember { mutableStateOf(task.assigned_to ?: "") }
    var status by remember { mutableStateOf(task.status) }
    var priority by remember { mutableStateOf(task.priority) }
    var estimatedHours by remember { mutableStateOf(task.estimated_hours.toString()) }
    var spentHours by remember { mutableStateOf(task.spent_hours.toString()) }
    var sprintId by remember { mutableStateOf(task.sprint_id ?: "") }
    var epicId by remember { mutableStateOf(task.epic_id) }
    var startDate by remember { mutableStateOf(task.start_date) }
    var dueDate by remember { mutableStateOf(task.due_date) }
    var completedDate by remember { mutableStateOf(task.completed_date) }
    
    // Date picker state
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    var showCompletedDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Update Task",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = { assignedTo = it },
                    label = { Text("Assigned To (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = epicId ?: "",
                    onValueChange = { epicId = it },
                    label = { Text("Epic ID") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true  // Usually epic ID shouldn't be edited directly
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status Dropdown
                var expandedStatus by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = it }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        listOf("To Do", "In Progress", "Done").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority Dropdown
                var expandedPriority by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = it }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedPriority,
                        onDismissRequest = { expandedPriority = false }
                    ) {
                        listOf("Low", "Medium", "High", "Critical").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = estimatedHours,
                        onValueChange = { 
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                estimatedHours = it
                            }
                        },
                        label = { Text("Estimated Hours") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    OutlinedTextField(
                        value = spentHours,
                        onValueChange = { 
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                spentHours = it
                            }
                        },
                        label = { Text("Spent Hours") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = epicId ?: "",
                    onValueChange = { epicId = it },
                    label = { Text("Epic ID") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true  // Usually epic ID shouldn't be edited directly
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = sprintId,
                    onValueChange = { sprintId = it },
                    label = { Text("Sprint ID (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Start Date
                OutlinedTextField(
                    value = startDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("Start Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Due Date
                OutlinedTextField(
                    value = dueDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("Due Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDueDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Completed Date
                OutlinedTextField(
                    value = completedDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("Completed Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showCompletedDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onUpdateTask(
                                    title,
                                    description.ifBlank { null },
                                    epicId,
                                    assignedTo.ifBlank { null },
                                    status,
                                    priority,
                                    estimatedHours.toDoubleOrNull() ?: 0.0,
                                    spentHours.toDoubleOrNull() ?: 0.0,
                                    startDate,
                                    dueDate,
                                    completedDate,
                                    sprintId.ifBlank { null }
                                )
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
    
    // Date Picker Dialogs - These need to be inside the UpdateTaskDialog function
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { startDate = Date(it) }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showDueDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDueDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dueDate = Date(it) }
                    showDueDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDueDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showCompletedDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCompletedDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { completedDate = Date(it) }
                    showCompletedDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showCompletedDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}