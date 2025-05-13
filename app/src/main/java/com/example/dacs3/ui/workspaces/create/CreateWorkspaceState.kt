package com.example.dacs3.ui.workspaces.create

import com.example.dacs3.data.model.Workspace

/**
 * State for the Create Workspace screen
 */
data class CreateWorkspaceState(
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val createdWorkspace: Workspace? = null,
    val error: String? = null
) 