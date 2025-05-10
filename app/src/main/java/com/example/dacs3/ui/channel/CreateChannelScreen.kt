package com.example.dacs3.ui.channel

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
import com.example.dacs3.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChannelScreen(
    workspaceId: String,
    viewModel: CreateChannelViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onChannelCreated: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var channelName by remember { mutableStateOf("") }
    var channelDescription by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Set the workspace ID when the screen is created
    LaunchedEffect(workspaceId) {
        viewModel.setWorkspaceId(workspaceId)
    }
    
    val isFormValid = channelName.isNotBlank()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FC))
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Create Channel") },
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
                text = "Create a new channel",
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
                    // Channel name input (with # prefix)
                    OutlinedTextField(
                        value = channelName,
                        onValueChange = { 
                            // Remove spaces and special characters
                            channelName = it.replace(Regex("[^a-zA-Z0-9-_]"), "").lowercase()
                        },
                        label = { Text("Channel Name") },
                        placeholder = { Text("e.g. general") },
                        leadingIcon = {
                            Text(
                                text = "#",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B4EFF),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = channelName.isBlank() && channelName.isNotEmpty()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Channel description input
                    OutlinedTextField(
                        value = channelDescription,
                        onValueChange = { channelDescription = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("What's this channel about?") },
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
                    
                    // Private channel switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPrivate) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (isPrivate) Color(0xFF6B4EFF) else Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Private Channel",
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Only invited people can join",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Switch(
                            checked = isPrivate,
                            onCheckedChange = { isPrivate = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF6B4EFF),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray
                            )
                        )
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
                        viewModel.createChannel(
                            name = channelName.trim(),
                            description = channelDescription.trim(),
                            isPrivate = isPrivate
                        ) { channelId ->
                            onChannelCreated(channelId)
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
                        text = "Create Channel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
} 