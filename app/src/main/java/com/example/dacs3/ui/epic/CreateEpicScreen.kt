package com.example.dacs3.ui.epic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.home.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEpicScreen(
    workspaceViewModel: WorkspaceViewModel = hiltViewModel(),
    viewModel: CreateEpicViewModel = hiltViewModel(),
    onEpicCreated: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var epicName by remember { mutableStateOf("") }
    var epicDescription by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(3) } // Medium priority by default
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Get the current workspace ID from WorkspaceViewModel
    val selectedWorkspaceId by workspaceViewModel.selectedWorkspaceId.collectAsState()
    val selectedWorkspace by workspaceViewModel.selectedWorkspace.collectAsState()
    
    // Set the workspace ID when available
    LaunchedEffect(selectedWorkspaceId) {
        selectedWorkspaceId?.let { workspaceId ->
            viewModel.setWorkspaceId(workspaceId)
        }
    }
    
    val isFormValid = epicName.isNotBlank() && selectedWorkspaceId != null
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Create Epic") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6B4EFF),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create a new epic",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 16.dp)
            )
            
            // Workspace information
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
                        text = "Selected Workspace",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B4EFF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedWorkspace?.name ?: "No workspace selected",
                        fontWeight = FontWeight.Medium
                    )
                    if (selectedWorkspace == null) {
                        Text(
                            text = "Please select a workspace first",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                    // Epic name input
                    OutlinedTextField(
                        value = epicName,
                        onValueChange = { epicName = it },
                        label = { Text("Epic Name") },
                        placeholder = { Text("e.g. Product Launch") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF6B4EFF)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = epicName.isBlank() && epicName.isNotEmpty()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Epic description input
                    OutlinedTextField(
                        value = epicDescription,
                        onValueChange = { epicDescription = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("What's this epic about?") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF6B4EFF)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Priority slider
                    Text(
                        text = "Priority: ${getPriorityText(priority)}",
                        fontWeight = FontWeight.Medium
                    )
                    
                    Slider(
                        value = priority.toFloat(),
                        onValueChange = { priority = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6B4EFF),
                            activeTrackColor = Color(0xFF6B4EFF),
                            activeTickColor = Color(0xFF6B4EFF),
                            inactiveTrackColor = Color(0xFFDDDDFF)
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Low", fontSize = 12.sp, color = Color.Gray)
                        Text("Medium", fontSize = 12.sp, color = Color.Gray)
                        Text("High", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            
            // Show error message if any
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Create button
            Button(
                onClick = {
                    if (isFormValid && !isLoading) {
                        viewModel.createEpic(
                            name = epicName.trim(),
                            description = epicDescription.trim(),
                            priority = priority
                        ) { epicId ->
                            onEpicCreated(epicId)
                        }
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF),
                    disabledContainerColor = Color(0xFF6B4EFF).copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Epic",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Helper function to get priority text
private fun getPriorityText(priority: Int): String {
    return when (priority) {
        1 -> "Very Low"
        2 -> "Low"
        3 -> "Medium"
        4 -> "High"
        5 -> "Critical"
        else -> "Medium"
    }
} 