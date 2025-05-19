//package com.example.dacs3.ui.sprint
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.CalendarToday
//import androidx.compose.material.icons.filled.FilterList
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.dacs3.data.model.Sprint
//import java.text.SimpleDateFormat
//import java.util.*
//import java.time.LocalDate
//import java.time.ZoneId
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SprintScreen(
//    workspaceId: String,
//    viewModel: SprintViewModel = hiltViewModel(),
//    onNavigateBack: () -> Unit,
//    onSprintSelected: (Sprint) -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    var showCreateDialog by remember { mutableStateOf(false) }
//    var showFilterDialog by remember { mutableStateOf(false) }
//
//    LaunchedEffect(workspaceId) {
//        viewModel.loadSprints(workspaceId)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Sprints") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { showFilterDialog = true }) {
//                        Icon(Icons.Default.FilterList, contentDescription = "Filter Sprints")
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { showCreateDialog = true }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Create Sprint")
//            }
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            } else if (uiState.filteredSprints.isEmpty()) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        text = "No sprints found",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Button(onClick = { showCreateDialog = true }) {
//                        Text("Create Sprint")
//                    }
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                ) {
//                    items(uiState.filteredSprints) { sprint ->
//                        SprintItem(
//                            sprint = sprint,
//                            onClick = { onSprintSelected(sprint) }
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//                }
//            }
//
//            // Show error message if any
//            uiState.error?.let { error ->
//                Snackbar(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp)
//                ) {
//                    Text(error)
//                }
//            }
//
//            // Create Sprint Dialog
//            if (showCreateDialog) {
//                CreateSprintDialog(
//                    onDismiss = { showCreateDialog = false },
//                    onCreateSprint = { name, goal, startDate, endDate, status ->
//                        viewModel.createSprint(
//                            name = name,
//                            goal = goal,
//                            workspaceId = workspaceId,
//                            startDate = startDate,
//                            endDate = endDate,
//                            status = status
//                        )
//                        showCreateDialog = false
//                    }
//                )
//            }
//
//            // Filter Dialog
//            if (showFilterDialog) {
//                FilterSprintDialog(
//                    onDismiss = { showFilterDialog = false },
//                    onApplyFilter = { status ->
//                        viewModel.filterSprintsByStatus(status)
//                        showFilterDialog = false
//                    }
//                )
//            }
//
//            // Success message
//            if (uiState.isCreationSuccessful) {
//                LaunchedEffect(uiState.isCreationSuccessful) {
//                    // Reset the state after showing success
//                    viewModel.resetCreationState()
//                }
//                Snackbar(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp)
//                ) {
//                    Text("Sprint created successfully!")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SprintItem(
//    sprint: Sprint,
//    onClick: () -> Unit
//) {
//    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(
//                text = sprint.name,
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            sprint.goal?.let {
//                Text(
//                    text = "Goal: $it",
//                    style = MaterialTheme.typography.bodyMedium,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//
//            // Status Chip
//            Surface(
//                shape = RoundedCornerShape(4.dp),
//                color = when(sprint.status.lowercase()) {
//                    "active" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
//                    "completed" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
//                    "planned" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
//                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
//                }
//            ) {
//                Text(
//                    text = sprint.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
//                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = when(sprint.status.lowercase()) {
//                        "active" -> MaterialTheme.colorScheme.primary
//                        "completed" -> MaterialTheme.colorScheme.tertiary
//                        "planned" -> MaterialTheme.colorScheme.secondary
//                        else -> MaterialTheme.colorScheme.outline
//                    }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Dates
//                Column {
//                    Text(
//                        text = "Start: ${dateFormat.format(sprint.start_date)}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//
//                    Text(
//                        text = "End: ${dateFormat.format(sprint.end_date)}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreateSprintDialog(
//    onDismiss: () -> Unit,
//    onCreateSprint: (String, String?, Date, Date, String) -> Unit
//) {
//    var name by remember { mutableStateOf("") }
//    var goal by remember { mutableStateOf("") }
//    var startDate by remember { mutableStateOf(Date()) }
//    var endDate by remember { mutableStateOf(Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000)) } // Default 2 weeks
//    var status by remember { mutableStateOf("Planned") }
//
//    // Date picker state
//    var showStartDatePicker by remember { mutableStateOf(false) }
//    var showEndDatePicker by remember { mutableStateOf(false) }
//
//    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Surface(
//            shape = RoundedCornerShape(16.dp),
//            color = MaterialTheme.colorScheme.surface
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(20.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(
//                    text = "Create Sprint",
//                    style = MaterialTheme.typography.titleLarge
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                OutlinedTextField(
//                    value = goal,
//                    onValueChange = { goal = it },
//                    label = { Text("Goal") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Start Date
//                OutlinedTextField(
//                    value = dateFormatter.format(startDate),
//                    onValueChange = { },
//                    label = { Text("Start Date") },
//                    modifier = Modifier.fillMaxWidth(),
//                    readOnly = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showStartDatePicker = true }) {
//                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
//                        }
//                    }
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // End Date
//                OutlinedTextField(
//                    value = dateFormatter.format(endDate),
//                    onValueChange = { },
//                    label = { Text("End Date") },
//                    modifier = Modifier.fillMaxWidth(),
//                    readOnly = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showEndDatePicker = true }) {
//                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
//                        }
//                    }
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Status Dropdown
//                var expandedStatus by remember { mutableStateOf(false) }
//                ExposedDropdownMenuBox(
//                    expanded = expandedStatus,
//                    onExpandedChange = { expandedStatus = it }
//                ) {
//                    OutlinedTextField(
//                        value = status,
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Status") },
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                    )
//
//                    ExposedDropdownMenu(
//                        expanded = expandedStatus,
//                        onDismissRequest = { expandedStatus = false }
//                    ) {
//                        listOf("Planned", "Active", "Completed").forEach { option ->
//                            DropdownMenuItem(
//                                text = { Text(option) },
//                                onClick = {
//                                    status = option
//                                    expandedStatus = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    TextButton(onClick = onDismiss) {
//                        Text("Cancel")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = {
//                            if (name.isNotBlank()) {
//                                onCreateSprint(
//                                    name,
//                                    goal.ifBlank { null },
//                                    startDate,
//                                    endDate,
//                                    status
//                                )
//                            }
//                        },
//                        enabled = name.isNotBlank()
//                    ) {
//                        Text("Create")
//                    }
//                }
//            }
//        }
//    }
//
//    // Start Date Picker Dialog
//    if (showStartDatePicker) {
//        val datePickerState = rememberDatePickerState(
//            initialSelectedDateMillis = startDate.time
//        )
//
//        DatePickerDialog(
//            onDismissRequest = { showStartDatePicker = false },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let {
//                            startDate = Date(it)
//                        }
//                        showStartDatePicker = false
//                    }
//                ) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showStartDatePicker = false }) {
//                    Text("Cancel")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//
//    // End Date Picker Dialog
//    if (showEndDatePicker) {
//        val datePickerState = rememberDatePickerState(
//            initialSelectedDateMillis = endDate.time
//        )
//
//        DatePickerDialog(
//            onDismissRequest = { showEndDatePicker = false },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let {
//                            endDate = Date(it)
//                        }
//                        showEndDatePicker = false
//                    }
//                ) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showEndDatePicker = false }) {
//                    Text("Cancel")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}
//
//@Composable
//fun FilterSprintDialog(
//    onDismiss: () -> Unit,
//    onApplyFilter: (status: String?) -> Unit
//) {
//    var selectedStatus by remember { mutableStateOf<String?>(null) }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Surface(
//            shape = RoundedCornerShape(16.dp),
//            color = MaterialTheme.colorScheme.surface
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(20.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(
//                    text = "Filter Sprints",
//                    style = MaterialTheme.typography.headlineSmall
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "Status",
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Column {
//                    listOf(null, "Planned", "Active", "Completed").forEach { status ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { selectedStatus = status }
//                                .padding(vertical = 8.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            RadioButton(
//                                selected = selectedStatus == status,
//                                onClick = { selectedStatus = status }
//                            )
//
//                            Spacer(modifier = Modifier.width(8.dp))
//
//                            Text(
//                                text = status ?: "All",
//                                style = MaterialTheme.typography.bodyLarge
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    TextButton(onClick = onDismiss) {
//                        Text("Cancel")
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    Button(
//                        onClick = { onApplyFilter(selectedStatus) }
//                    ) {
//                        Text("Apply")
//                    }
//                }
//            }
//        }
//    }
//}