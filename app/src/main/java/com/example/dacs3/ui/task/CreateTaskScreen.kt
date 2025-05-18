package com.example.dacs3.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.task.TaskViewModel
import com.example.dacs3.domain.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    workspaceId: String,
    epicId: String? = null,
    onNavigateBack: () -> Unit,
    onTaskCreated: (String) -> Unit,
    viewModel: TaskViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()
    
    // Trạng thái cho form
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("todo") }
    var selectedPriority by remember { mutableStateOf("medium") }
    var estimatedHours by remember { mutableStateOf("0") }
    var selectedAssigneeId by remember { mutableStateOf<String?>(null) }
    var selectedSprintId by remember { mutableStateOf<String?>(null) }
    
    // Dropdown states
    var statusExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var assigneeExpanded by remember { mutableStateOf(false) }
    var sprintExpanded by remember { mutableStateOf(false) }
    var epicExpanded by remember { mutableStateOf(false) }
    
    // Danh sách các trạng thái và ưu tiên
    val statusOptions = listOf("todo", "in_progress", "review", "done")
    val priorityOptions = listOf("low", "medium", "high", "urgent")
    
    // Load danh sách epic và người dùng khi màn hình được tạo
    LaunchedEffect(workspaceId) {
        viewModel.loadEpicsForWorkspace(workspaceId)
        userViewModel.loadWorkspaceMembers(workspaceId)
    }
    
    // Xử lý khi tạo task thành công
    LaunchedEffect(uiState.isCreationSuccessful) {
        if (uiState.isCreationSuccessful && uiState.selectedTask != null) {
            onTaskCreated(uiState.selectedTask!!._id)
        }
    }
    
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        val (topBar, content) = createRefs()
        
        // Top Bar
        TopAppBar(
            title = { Text("Tạo công việc mới") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            modifier = Modifier.constrainAs(topBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        
        // Content
        Column(
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(topBar.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Tiêu đề
                    Text(
                        text = "Thông tin công việc",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tên công việc
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tên công việc") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.isEmpty() && uiState.error != null
                    )

                    if (title.isEmpty() && uiState.error != null) {
                        Text(
                            text = "Tên công việc không được để trống",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mô tả
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Mô tả") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Epic dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.availableEpics.find { it._id == epicId }?.title ?: "Chọn Epic",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Epic") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { epicExpanded = true }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = epicExpanded,
                            onDismissRequest = { epicExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            uiState.availableEpics.forEach { epic ->
                                DropdownMenuItem(
                                    text = { Text(epic.title) },
                                    onClick = {
                                        epicId = epic._id
                                        epicExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trạng thái dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStatus,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Trạng thái") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { statusExpanded = true }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        selectedStatus = status
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ưu tiên dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedPriority,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ưu tiên") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { priorityExpanded = true }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            priorityOptions.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority) },
                                    onClick = {
                                        selectedPriority = priority
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Người được gán
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = userState.workspaceMembers.find { it._id == selectedAssigneeId }?.name ?: "Chọn người thực hiện",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Người thực hiện") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { assigneeExpanded = true }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = assigneeExpanded,
                            onDismissRequest = { assigneeExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            userState.workspaceMembers.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.name ?: "Unknown") },
                                    onClick = {
                                        selectedAssigneeId = user._id
                                        assigneeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Số giờ ước tính
                    OutlinedTextField(
                        value = estimatedHours,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                estimatedHours = it
                            }
                        },
                        label = { Text("Số giờ ước tính") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Nút tạo công việc
                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                viewModel.createTask(
                                    title = title,
                                    description = description.ifEmpty { null },
                                    workspaceId = workspaceId,
                                    epicId = epicId,
                                    assignedTo = selectedAssigneeId,
                                    status = selectedStatus,
                                    priority = selectedPriority,
                                    estimatedHours = estimatedHours.toDoubleOrNull() ?: 0,
                                    sprintId = selectedSprintId,
                                    startDate = null,
                                    dueDate = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = title.isNotEmpty() && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Tạo công việc")
                        }
                    }

                    // Hiển thị lỗi nếu có
                    uiState.error?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}