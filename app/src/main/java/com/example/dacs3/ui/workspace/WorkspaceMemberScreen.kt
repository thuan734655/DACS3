package com.example.dacs3.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.WorkspaceMember
import com.example.dacs3.ui.theme.PrimaryBlue
import com.example.dacs3.ui.theme.TeamNexusPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceMemberScreen(
    workspaceId: String,
    onNavigateBack: () -> Unit,
    viewModel: WorkspaceMemberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Initialize the ViewModel with the workspaceId
    LaunchedEffect(workspaceId) {
        viewModel.loadWorkspaceMembers(workspaceId)
        viewModel.loadAvailableUsers()
    }
    
    // States for add member dialog
    var showAddMemberDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workspace Members") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddMemberDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Member"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Members (${uiState.members.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(uiState.members) { member ->
                        MemberItem(
                            member = member,
                            onRemoveMember = { 
                                viewModel.setSelectedMember(member)
                                viewModel.showRemoveMemberDialog()
                            },
                            onUpdateRole = { newRole ->
                                viewModel.updateMemberRole(member.user_id._id, newRole)
                            }
                        )
                    }
                }
                
                // Error message
                uiState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(error)
                    }
                }
                
                // Success message
                if (uiState.isAddMemberSuccessful) {
                    LaunchedEffect(Unit) {
                        viewModel.resetSuccessState()
                    }
                    
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { viewModel.resetSuccessState() }) {
                                Text("Dismiss")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("Member added successfully")
                    }
                }
                
                if (uiState.isRemoveMemberSuccessful) {
                    LaunchedEffect(Unit) {
                        viewModel.resetSuccessState()
                    }
                    
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { viewModel.resetSuccessState() }) {
                                Text("Dismiss")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("Member removed successfully")
                    }
                }
                
                if (uiState.isUpdateRoleSuccessful) {
                    LaunchedEffect(Unit) {
                        viewModel.resetSuccessState()
                    }
                    
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { viewModel.resetSuccessState() }) {
                                Text("Dismiss")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("Member role updated successfully")
                    }
                }
            }
        }
        
        // Add Member Dialog
        if (showAddMemberDialog) {
            AddMemberDialog(
                availableUsers = uiState.availableUsers,
                onDismiss = { showAddMemberDialog = false },
                onAddMember = { userId, role ->
                    viewModel.addMember(userId, role)
                    showAddMemberDialog = false
                }
            )
        }
        
        // Remove Member Dialog
        if (uiState.showRemoveMemberDialog) {
            RemoveMemberDialog(
                member = uiState.selectedMember,
                onDismiss = { viewModel.hideRemoveMemberDialog() },
                onConfirm = {
                    uiState.selectedMember?.let { member ->
                        viewModel.removeMember(member.user_id._id)
                    }
                    viewModel.hideRemoveMemberDialog()
                }
            )
        }
    }
}

@Composable
fun MemberItem(
    member: WorkspaceMember,
    onRemoveMember: () -> Unit,
    onUpdateRole: (String) -> Unit
) {
    var showRoleDropdown by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(TeamNexusPurple.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.user_id.name.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // User info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = member.user_id.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Email might be null, show placeholder if null
                Text(
                    text = "No email available", // Email not in User model
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Role dropdown
            Box {
                TextButton(
                    onClick = { showRoleDropdown = true }
                ) {
                    Text(member.role)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Change Role"
                    )
                }
                
                DropdownMenu(
                    expanded = showRoleDropdown,
                    onDismissRequest = { showRoleDropdown = false }
                ) {
                    listOf("admin", "member", "guest").forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                onUpdateRole(role)
                                showRoleDropdown = false
                            }
                        )
                    }
                }
            }
            
            IconButton(onClick = onRemoveMember) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Member",
                    tint = Color.Red.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    availableUsers: List<User>,
    onDismiss: () -> Unit,
    onAddMember: (userId: String, role: String) -> Unit
) {
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedRole by remember { mutableStateOf("member") }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredUsers = remember(searchQuery, availableUsers) {
        if (searchQuery.isBlank()) {
            availableUsers
        } else {
            availableUsers.filter { 
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Member",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Users") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // User list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { selectedUser = user },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedUser?._id == user._id,
                                onClick = { selectedUser = user }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Column {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                // Handle email which might be null
                                Text(
                                    text = "No email available", // Email not in User model
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Role selection
                Text(
                    text = "Role",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("admin", "member", "guest").forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(role) },
                            leadingIcon = if (selectedRole == role) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            selectedUser?.let { user ->
                                onAddMember(user._id, selectedRole)
                            }
                        },
                        enabled = selectedUser != null
                    ) {
                        Text("Add Member")
                    }
                }
            }
        }
    }
}

@Composable
fun RemoveMemberDialog(
    member: WorkspaceMember?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (member == null) return
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Member") },
        text = { 
            Column {
                Text("Are you sure you want to remove this member from the workspace?") 
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = member.user_id.name, 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Remove")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
