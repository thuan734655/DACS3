package com.example.dacs3.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.WorkspaceResponse
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkspaceUiState(
    val isLoading: Boolean = false,
    val workspaces: List<com.example.dacs3.data.model.Workspace> = emptyList(),
    val error: String? = null,
    val isCreationSuccessful: Boolean = false,
    val selectedWorkspace: com.example.dacs3.data.model.Workspace? = null
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
    private val sessionManager: SessionManager,

) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkspaceUiState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    init {
        loadWorkspaces()
    }

    fun loadWorkspaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // First try to get from API
                val response = workspaceRepository.getAllWorkspacesFromApi()
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workspaces = response.data ?: emptyList()
                        )
                    }
                } else {
                    // If API fails, fall back to locally cached data
                    workspaceRepository.getAll().collect { workspaceEntities ->
                        val workspaces = workspaceEntities.map { it.toWorkspace() }
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                workspaces = workspaces
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // On exception, try to load from local database
                workspaceRepository.getAll().collect { workspaceEntities ->
                    val workspaces = workspaceEntities.map { it.toWorkspace() }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workspaces = workspaces,
                            error = "Could not connect to server. Showing cached data."
                        )
                    }
                }
            }
        }
    }

    fun createWorkspace(name: String, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isCreationSuccessful = false) }
            
            try {
                val userId = sessionManager.getUserId() ?: throw IllegalStateException("User not logged in")
                
                val response = workspaceRepository.createWorkspace(
                    name = name,
                    description = description
                )
                
                if (response.success && response.data != null) {
                    // Update UI state with the new workspace
                    val updatedWorkspaces = _uiState.value.workspaces.toMutableList()
                    updatedWorkspaces.add(response.data)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workspaces = updatedWorkspaces,
                            isCreationSuccessful = true,
                            selectedWorkspace = response.data
                        )
                    }
                    
                    // Refresh workspaces list
                    loadWorkspaces()
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to create workspace"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectWorkspace(workspace: com.example.dacs3.data.model.Workspace) {
        _uiState.update { it.copy(selectedWorkspace = workspace) }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedWorkspace = null) }
    }
    
    fun resetCreationState() {
        _uiState.update { it.copy(isCreationSuccessful = false, error = null) }
    }
    
    fun getWorkspaceById(workspaceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
                
                if (response.success && response.data != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            selectedWorkspace = response.data
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load workspace details"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
} 