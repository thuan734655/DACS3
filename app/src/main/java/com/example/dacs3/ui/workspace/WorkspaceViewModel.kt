package com.example.dacs3.ui.workspace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.model.WorkspaceDetailData
import com.example.dacs3.data.model.WorkspaceResponse
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.repository.impl.WorkspaceRepositoryImpl
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.data.user.UserManager
import com.example.dacs3.util.WorkspacePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkspaceUiState(
    val isLoading: Boolean = false,
    val workspaces: List<Workspace> = emptyList(),
    val error: String? = null,
    val isCreationSuccessful: Boolean = false,
    val selectedWorkspace: Workspace? = null,
    val workspaceDetail: WorkspaceDetailData? = null
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepositoryImpl,
    private val sessionManager: SessionManager,
    private val workspacePreferences: WorkspacePreferences
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
                val workspace_id = workspacePreferences.getSelectedWorkspaceId()
                val response = workspaceRepository.getWorkspaceByIdFromApi(workspace_id)
                
                if (response.success && response.data != null) {
                    // Extract workspaces list from the response
                    val workspacesList = response.data.workspace
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workspaces = listOf(workspacesList)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("WorkspaceViewModel", "Error loading workspaces from API")
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
                    val newWorkspace = response.data.workspace
                    val updatedWorkspaces = _uiState.value.workspaces.toMutableList()
                    updatedWorkspaces.add(newWorkspace)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workspaces = updatedWorkspaces,
                            isCreationSuccessful = true,
                            selectedWorkspace = newWorkspace,
                            workspaceDetail = response.data
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

    fun selectWorkspace(workspace: Workspace) {
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
                            selectedWorkspace = response.data.workspace,
                            workspaceDetail = response.data
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