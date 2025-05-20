package com.example.dacs3.ui.invitation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.Invitation
import com.example.dacs3.data.model.Workspace
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWorkspace: (String) -> Unit,
    viewModel: InvitationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedFilter by remember { mutableStateOf("pending") }
    
    // Load invitations when screen is displayed
    LaunchedEffect(selectedFilter) {
        viewModel.loadInvitations(selectedFilter)
    }
    
    // Handle success actions
    LaunchedEffect(state.actionSuccess) {
        if (state.actionSuccess) {
            viewModel.resetActionState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invitations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadInvitations(selectedFilter) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Filter tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "pending",
                    onClick = { selectedFilter = "pending" },
                    label = { Text("Pending") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                FilterChip(
                    selected = selectedFilter == "accepted",
                    onClick = { selectedFilter = "accepted" },
                    label = { Text("Accepted") }
                )
                
                FilterChip(
                    selected = selectedFilter == "rejected",
                    onClick = { selectedFilter = "rejected" },
                    label = { Text("Rejected") },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Error message
            if (state.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Error loading invitations: ${state.error ?: ""}",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Action error message
            AnimatedVisibility(visible = state.actionError != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.actionError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Loading indicator
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.invitations.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedFilter) {
                            "pending" -> "No pending invitations"
                            "accepted" -> "No accepted invitations"
                            "rejected" -> "No rejected invitations"
                            else -> "No invitations"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // List of invitations
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.invitations) { invitation ->
                        InvitationItem(
                            invitation = invitation,
                            isProcessing = state.processingInvitationId == invitation.id,
                            onAccept = { 
                                if (invitation.status == "pending") {
                                    viewModel.acceptInvitation(invitation.id)
                                }
                            },
                            onReject = { 
                                if (invitation.status == "pending") {
                                    viewModel.rejectInvitation(invitation.id)
                                }
                            },
                            onNavigateToWorkspace = { workspaceId ->
                                if (invitation.status == "accepted" && workspaceId is String) {
                                    onNavigateToWorkspace(workspaceId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InvitationItem(
    invitation: Invitation,
    isProcessing: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNavigateToWorkspace: (Any) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = invitation.status == "accepted" && invitation.workspaceId is String,
                onClick = { onNavigateToWorkspace(invitation.workspaceId) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = when (invitation.status) {
                "pending" -> MaterialTheme.colorScheme.surfaceVariant
                "accepted" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                "rejected" -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Workspace info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            invitation.workspaceId is Workspace -> invitation.workspaceId.name
                            else -> "Workspace"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Invited by: ${
                            when {
                                invitation.invitedBy is Map<*, *> && invitation.invitedBy["username"] != null -> 
                                    invitation.invitedBy["username"].toString()
                                else -> "Unknown"
                            }
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (invitation.status) {
                                "pending" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                "accepted" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                "rejected" -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = when (invitation.status) {
                                "pending" -> MaterialTheme.colorScheme.tertiary
                                "accepted" -> MaterialTheme.colorScheme.primary
                                "rejected" -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.outline
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (invitation.status) {
                            "pending" -> "Pending"
                            "accepted" -> "Accepted"
                            "rejected" -> "Rejected"
                            else -> invitation.status
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when (invitation.status) {
                            "pending" -> MaterialTheme.colorScheme.tertiary
                            "accepted" -> MaterialTheme.colorScheme.primary
                            "rejected" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date info
            Text(
                text = "Created: ${dateFormat.format(invitation.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (invitation.status != "pending") {
                Text(
                    text = "Updated: ${dateFormat.format(invitation.updatedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Actions if pending
            if (invitation.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Reject",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reject")
                        }
                        
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Accept"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Accept")
                        }
                    }
                }
            }
            
            // If accepted, show "Go to workspace" hint
            if (invitation.status == "accepted") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to view workspace details",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
