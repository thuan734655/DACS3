package com.example.dacs3.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkspaceScreen(
    viewModel: WorkspaceListViewModel = hiltViewModel(),
    onWorkspaceCreated: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var workspaceName by remember { mutableStateOf("") }
    var workspaceDescription by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    
    val isFormValid = workspaceName.isNotBlank() && workspaceDescription.isNotBlank()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Create Workspace") },
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
                text = "Create a new workspace",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 16.dp)
            )
            
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
                    // Workspace name input
                    OutlinedTextField(
                        value = workspaceName,
                        onValueChange = { workspaceName = it },
                        label = { Text("Workspace Name") },
                        placeholder = { Text("Enter workspace name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = Color(0xFF6B4EFF)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = workspaceName.isBlank() && workspaceName.isNotEmpty()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Workspace description input
                    OutlinedTextField(
                        value = workspaceDescription,
                        onValueChange = { workspaceDescription = it },
                        label = { Text("Description") },
                        placeholder = { Text("Enter workspace description") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF6B4EFF)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        isError = workspaceDescription.isBlank() && workspaceDescription.isNotEmpty()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Create button
            Button(
                onClick = {
                    if (isFormValid && !isCreating) {
                        isCreating = true
                        // Use a hardcoded user ID for now
                        val workspaceId = viewModel.createWorkspace(
                            name = workspaceName.trim(),
                            description = workspaceDescription.trim(),
                            currentUserId = "user1"
                        )
                        onWorkspaceCreated(workspaceId)
                    }
                },
                enabled = isFormValid && !isCreating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF),
                    disabledContainerColor = Color(0xFF6B4EFF).copy(alpha = 0.5f)
                )
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Workspace",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
} 