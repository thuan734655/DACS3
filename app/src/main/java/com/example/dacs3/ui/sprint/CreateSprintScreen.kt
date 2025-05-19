package com.example.dacs3.ui.sprint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSprintScreen(
    viewModel: SprintViewModel = hiltViewModel(),
    workspaceId: String,
    onNavigateBack: () -> Unit,
    onSprintCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var sprintName by remember { mutableStateOf("") }
    var sprintDescription by remember { mutableStateOf("") }
    var sprintGoal by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Calendar.getInstance().time) }
    var endDate by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 14) }.time) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showNameError by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    LaunchedEffect(workspaceId) {
        viewModel.setWorkspaceId(workspaceId)
    }
    
    LaunchedEffect(uiState.isCreationSuccessful) {
        if (uiState.isCreationSuccessful) {
            onSprintCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Sprint") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Sprint Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Sprint Name
                        OutlinedTextField(
                            value = sprintName,
                            onValueChange = { 
                                sprintName = it
                                showNameError = false
                            },
                            label = { Text("Sprint Name") },
                            placeholder = { Text("Enter sprint name") },
                            isError = showNameError,
                            supportingText = if (showNameError) {
                                { Text("Please enter sprint name") }
                            } else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Sprint Description
                        OutlinedTextField(
                            value = sprintDescription,
                            onValueChange = { sprintDescription = it },
                            label = { Text("Description") },
                            placeholder = { Text("Enter sprint description (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Sprint Goal
                        OutlinedTextField(
                            value = sprintGoal,
                            onValueChange = { sprintGoal = it },
                            label = { Text("Goal") },
                            placeholder = { Text("Enter sprint goal (optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Start Date
                        OutlinedTextField(
                            value = dateFormat.format(startDate),
                            onValueChange = { },
                            label = { Text("Start Date") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showStartDatePicker = true }) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Select start date"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // End Date
                        OutlinedTextField(
                            value = dateFormat.format(endDate),
                            onValueChange = { },
                            label = { Text("End Date") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showEndDatePicker = true }) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Select end date"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Create Sprint Button
                Button(
                    onClick = {
                        if (sprintName.isBlank()) {
                            showNameError = true
                            return@Button
                        }
                        
                        viewModel.createSprint(
                            name = sprintName,
                            description = if (sprintDescription.isBlank()) null else sprintDescription,
                            startDate = startDate,
                            endDate = endDate,
                            goal = if (sprintGoal.isBlank()) null else sprintGoal
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Sprint")
                    }
                }
            }
            
            // Display error if any
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
            
            // Date Picker for start date
            if (showStartDatePicker) {
                DatePickerDialog(
                    initialDate = startDate,
                    onDateSelected = { 
                        startDate = it
                        showStartDatePicker = false
                    },
                    onDismiss = { showStartDatePicker = false }
                )
            }
            
            // Date Picker for end date
            if (showEndDatePicker) {
                DatePickerDialog(
                    initialDate = endDate,
                    onDateSelected = { 
                        endDate = it
                        showEndDatePicker = false
                    },
                    onDismiss = { showEndDatePicker = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        time = initialDate
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }.time
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}