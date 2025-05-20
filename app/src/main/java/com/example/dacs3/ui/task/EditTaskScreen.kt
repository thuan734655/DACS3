package com.example.dacs3.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.components.DatePickerDialog
import com.example.dacs3.ui.components.LoadingIndicator
import com.example.dacs3.util.formatDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: String,
    viewModel: TaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // Load task data when screen is shown
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val task = uiState.selectedTask
    
    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var estimatedHours by remember { mutableStateOf("") }
    var spentHours by remember { mutableStateOf("") }
    var selectedDueDate by remember { mutableStateOf<Date?>(null) }
    
    // Initialize form with task data when available
    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description ?: ""
            priority = it.priority
            status = it.status
            estimatedHours = it.estimated_hours.toString()
            spentHours = it.spent_hours.toString()
            selectedDueDate = it.due_date
        }
    }
    
    // Date picker dialog
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Priority and Status options
    val priorityOptions = listOf("LOW", "MEDIUM", "HIGH")
    val statusOptions = listOf("TO_DO", "IN_PROGRESS", "DONE")
    
    // Handle task saved event
    LaunchedEffect(uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            onNavigateBack()
            viewModel.resetSaveState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
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
                LoadingIndicator()
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "An error occurred",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (task != null) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    val (formCard, saveBtn) = createRefs()
                    
                    // Form content
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(formCard) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Title field
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Description field
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Priority dropdown
                            Text("Priority", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            var priorityExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = priorityExpanded,
                                onExpandedChange = { priorityExpanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = when (priority) {
                                        "LOW" -> "Low"
                                        "MEDIUM" -> "Medium"
                                        "HIGH" -> "High"
                                        else -> "Low"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                
                                ExposedDropdownMenu(
                                    expanded = priorityExpanded,
                                    onDismissRequest = { priorityExpanded = false }
                                ) {
                                    priorityOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = when (option) {
                                                        "LOW" -> "Low"
                                                        "MEDIUM" -> "Medium"
                                                        "HIGH" -> "High"
                                                        else -> option
                                                    }
                                                )
                                            },
                                            onClick = {
                                                priority = option
                                                priorityExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Status dropdown
                            Text("Status", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            var statusExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = statusExpanded,
                                onExpandedChange = { statusExpanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = when (status) {
                                        "TO_DO" -> "To Do"
                                        "IN_PROGRESS" -> "In Progress"
                                        "DONE" -> "Completed"
                                        else -> "To Do"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                
                                ExposedDropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false }
                                ) {
                                    statusOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = when (option) {
                                                        "TO_DO" -> "To Do"
                                                        "IN_PROGRESS" -> "In Progress"
                                                        "DONE" -> "Completed"
                                                        else -> option
                                                    }
                                                )
                                            },
                                            onClick = {
                                                status = option
                                                statusExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Time tracking
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Estimated hours
                                OutlinedTextField(
                                    value = estimatedHours,
                                    onValueChange = { 
                                        // Only allow numeric input
                                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                            estimatedHours = it
                                        }
                                    },
                                    label = { Text("Estimated Hours") },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                // Spent hours
                                OutlinedTextField(
                                    value = spentHours,
                                    onValueChange = { 
                                        // Only allow numeric input
                                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                            spentHours = it
                                        }
                                    },
                                    label = { Text("Spent Hours") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Due Date selector
                            Text("Due Date", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            OutlinedTextField(
                                value = selectedDueDate?.let { formatDate(it) } ?: "No due date selected",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select Due Date"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Save button
                    Button(
                        onClick = {
                            viewModel.updateTask(
                                id = taskId,
                                title = title,
                                description = if (description.isNotEmpty()) description else null,
                                epicId = task.epic_id,
                                assignedTo = task.assigned_to?._id, 
                                status = status,
                                priority = priority,
                                estimatedHours = estimatedHours.toIntOrNull(),
                                spentHours = spentHours.toIntOrNull(),
                                dueDate = selectedDueDate
                            )
                        },
                        enabled = title.isNotEmpty() && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(saveBtn) {
                                top.linkTo(formCard.bottom, margin = 16.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Update Task")
                        }
                    }
                }
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val context = LocalContext.current
        DatePickerDialog(
            context = context,
            initialDate = selectedDueDate ?: Date(),
            onDateSelected = {
                selectedDueDate = it
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}