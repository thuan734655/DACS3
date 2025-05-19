package com.example.dacs3.ui.sprint

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSprintScreen(
    sprintId: String,
    viewModel: SprintViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val selectedSprint = uiState.sprints.find { it._id == sprintId }
    
    // Load sprint details if not already loaded
    LaunchedEffect(sprintId) {
        if (selectedSprint == null) {
            viewModel.loadSprintDetail(sprintId)
        }
    }
    
    // State for form fields
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    
    // Set initial values when sprint is loaded
    LaunchedEffect(selectedSprint) {
        selectedSprint?.let {
            name = it.name
            description = it.description ?: ""
            goal = it.goal ?: ""
            startDate = it.start_date
            endDate = it.end_date
        }
    }
    
    // Date picker dialogs
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    // Date formatter
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    // Success handling
    LaunchedEffect(uiState.isUpdateSuccessful) {
        if (uiState.isUpdateSuccessful) {
            Toast.makeText(context, "Sprint updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateState()
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Sprint") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedSprint != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Sprint Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    singleLine = false
                )
                
                // Goal field
                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Sprint Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                
                // Start date
                OutlinedTextField(
                    value = dateFormatter.format(startDate),
                    onValueChange = { },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    },
                    readOnly = true
                )
                
                // End date
                OutlinedTextField(
                    value = dateFormatter.format(endDate),
                    onValueChange = { },
                    label = { Text("End Date") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    },
                    readOnly = true
                )
                
                // Submit button
                Button(
                    onClick = {
                        viewModel.updateSprint(
                            id = sprintId,
                            name = name,
                            description = description,
                            startDate = startDate,
                            endDate = endDate,
                            goal = goal
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Update Sprint")
                }
                
                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Sprint not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Sprint not found")
            }
        }
        
        // Date pickers
        if (showStartDatePicker) {
            DatePickerDialog(
                onDateSelected = { 
                    startDate = it
                    showStartDatePicker = false
                },
                onDismiss = { showStartDatePicker = false },
                initialDate = startDate
            )
        }
        
        if (showEndDatePicker) {
            DatePickerDialog(
                onDateSelected = { 
                    endDate = it
                    showEndDatePicker = false
                },
                onDismiss = { showEndDatePicker = false },
                initialDate = endDate
            )
        }
    }
}
