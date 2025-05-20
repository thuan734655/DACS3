package com.example.dacs3.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.components.DatePickerDialog
import com.example.dacs3.util.formatDate
import com.example.dacs3.util.toDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    workspaceId: String,
    epicId: String? = null,
    viewModel: TaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var status by remember { mutableStateOf("Todo") }
    var estimatedHours by remember { mutableStateOf("0") }
    var selectedDueDate by remember { mutableStateOf<Date?>(null) }
    var selectedAssignedUserId by remember { mutableStateOf<String?>(null) }
    
    // Date picker dialog
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Priority and Status options - phải khớp chính xác với định nghĩa từ server
    val priorityOptions = listOf("Low", "Medium", "High", "Critical")
    val statusOptions = listOf("Todo", "In Progress", "Done")
    
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
                title = { Text("Create Task") },
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
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
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
                            value = priority, // Đã sửa để dùng giá trị priority trực tiếp
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
                                        Text(text = option)
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
                            value = status, // Sử dụng giá trị trực tiếp từ status
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
                                        Text(text = option)
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
                    
                    // Assigned To field (không bắt buộc)
                    Text("Assigned To (Optional)", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    OutlinedTextField(
                        value = selectedAssignedUserId ?: "",
                        onValueChange = { selectedAssignedUserId = it.ifEmpty { null } },
                        label = { Text("User ID") },
                        placeholder = { Text("Enter user ID to assign") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = false,
                        supportingText = { Text("Leave empty if not assigning to anyone") }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
                        modifier = Modifier.fillMaxWidth()
                    )
                    
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
                    viewModel.createTask(
                        title = title,
                        description = if (description.isNotEmpty()) description else null,
                        workspaceId = workspaceId,
                        epicId = epicId,
                        assignedTo = selectedAssignedUserId, 
                        status = status,
                        priority = priority,
                        estimatedHours = estimatedHours.toIntOrNull(),
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
                    Text("Save Task")
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
