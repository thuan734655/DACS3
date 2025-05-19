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
                title = { Text("Tạo Sprint mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
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
                            text = "Thông tin Sprint",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Tên Sprint
                        OutlinedTextField(
                            value = sprintName,
                            onValueChange = { 
                                sprintName = it
                                showNameError = false
                            },
                            label = { Text("Tên Sprint") },
                            placeholder = { Text("Nhập tên sprint") },
                            isError = showNameError,
                            supportingText = if (showNameError) {
                                { Text("Tên sprint không được để trống") }
                            } else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Mô tả Sprint
                        OutlinedTextField(
                            value = sprintDescription,
                            onValueChange = { sprintDescription = it },
                            label = { Text("Mô tả (tùy chọn)") },
                            placeholder = { Text("Nhập mô tả cho sprint") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Mục tiêu Sprint
                        OutlinedTextField(
                            value = sprintGoal,
                            onValueChange = { sprintGoal = it },
                            label = { Text("Mục tiêu (tùy chọn)") },
                            placeholder = { Text("Nhập mục tiêu của sprint") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Ngày bắt đầu
                        OutlinedTextField(
                            value = dateFormat.format(startDate),
                            onValueChange = { },
                            label = { Text("Ngày bắt đầu") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showStartDatePicker = true }) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Chọn ngày bắt đầu"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Ngày kết thúc
                        OutlinedTextField(
                            value = dateFormat.format(endDate),
                            onValueChange = { },
                            label = { Text("Ngày kết thúc") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showEndDatePicker = true }) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Chọn ngày kết thúc"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Nút tạo Sprint
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
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Tạo Sprint")
                    }
                }
            }
            
            // Hiển thị lỗi nếu có
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
            
            // Date Picker cho ngày bắt đầu
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
            
            // Date Picker cho ngày kết thúc
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
                Text("Hủy")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}