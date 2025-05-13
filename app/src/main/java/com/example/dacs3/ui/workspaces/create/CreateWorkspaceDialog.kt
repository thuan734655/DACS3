package com.example.dacs3.ui.workspaces.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.theme.TeamNexusPurple
import kotlinx.coroutines.delay

/**
 * A dialog for creating a new workspace
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkspaceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onWorkspaceCreated: () -> Unit = {},
    viewModel: CreateWorkspaceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Check if workspace was created
    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            delay(500)
            onWorkspaceCreated()
            onDismiss()
        }
    }
    
    // Show dialog only when requested
    if (showDialog) {
        Dialog(
            onDismissRequest = { 
                if (!state.isLoading) onDismiss() 
            },
            properties = DialogProperties(
                dismissOnBackPress = !state.isLoading,
                dismissOnClickOutside = !state.isLoading
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Dialog header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create Workspace",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        IconButton(
                            onClick = { if (!state.isLoading) onDismiss() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dialog content
                    WorkspaceForm(
                        name = state.name,
                        description = state.description,
                        onNameChange = viewModel::updateName,
                        onDescriptionChange = viewModel::updateDescription
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Create button
                    Button(
                        onClick = viewModel::createWorkspace,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.name.isNotBlank() && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TeamNexusPurple,
                            disabledContainerColor = TeamNexusPurple.copy(alpha = 0.6f)
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "CREATE WORKSPACE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = state.error != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = state.error ?: "",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = state.isCreated,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = "Workspace created successfully!",
                            color = Color.Green,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkspaceForm(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Workspace Name") },
            placeholder = { Text("e.g. Team Nexus") },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = TeamNexusPurple,
                focusedLabelColor = TeamNexusPurple,
                cursorColor = TeamNexusPurple
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        
        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description (Optional)") },
            placeholder = { Text("Briefly describe your workspace") },
            minLines = 2,
            maxLines = 3,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = TeamNexusPurple,
                focusedLabelColor = TeamNexusPurple,
                cursorColor = TeamNexusPurple
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
} 