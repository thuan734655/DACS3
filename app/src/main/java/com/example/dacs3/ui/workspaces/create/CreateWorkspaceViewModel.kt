package com.example.dacs3.ui.workspaces.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Create Workspace screen
 */
@HiltViewModel
class CreateWorkspaceViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CreateWorkspaceState())
    val state: StateFlow<CreateWorkspaceState> = _state.asStateFlow()
    
    /**
     * Update the workspace name
     */
    fun updateName(name: String) {
        _state.update { currentState ->
            currentState.copy(name = name)
        }
    }
    
    /**
     * Update the workspace description
     */
    fun updateDescription(description: String) {
        _state.update { currentState ->
            currentState.copy(description = description)
        }
    }
    
    /**
     * Clear any error message
     */
    fun clearError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }
    
    /**
     * Create a new workspace
     */
    fun createWorkspace() {
        // Validate input
        val name = state.value.name.trim()
        val description = state.value.description.trim()
        
        if (name.isBlank()) {
            _state.update { currentState ->
                currentState.copy(error = "Workspace name cannot be empty")
            }
            return
        }
        
        // Show loading state
        _state.update { currentState ->
            currentState.copy(isLoading = true)
        }
        
        // Create workspace via repository
        viewModelScope.launch {
            val result = workspaceRepository.createWorkspace(name, description)
            
            when (result) {
                is Resource.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isCreated = true,
                            createdWorkspace = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to create workspace"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }
} 