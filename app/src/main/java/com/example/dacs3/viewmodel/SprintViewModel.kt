package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.repository.SprintRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class SprintUiState(
    val isLoading: Boolean = false,
    val sprints: List<Sprint> = emptyList(),
    val filteredSprints: List<Sprint> = emptyList(),
    val error: String? = null,
    val isCreationSuccessful: Boolean = false,
    val selectedSprint: Sprint? = null,
    val currentWorkspaceId: String? = null
)

@HiltViewModel
class SprintViewModel @Inject constructor(
    private val sprintRepository: SprintRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SprintUiState())
    val uiState: StateFlow<SprintUiState> = _uiState.asStateFlow()

    fun loadSprints(workspaceId: String) {
        _uiState.update { it.copy(currentWorkspaceId = workspaceId) }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // First try to get from API
                val response = sprintRepository.getAllSprintsFromApi(workspaceId = workspaceId)
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            sprints = response.data ?: emptyList(),
                            filteredSprints = response.data ?: emptyList()
                        )
                    }
                } else {
                    // If API fails, fall back to locally cached data
                    sprintRepository.getSprintsByWorkspaceId(workspaceId).collect { sprintEntities ->
                        val sprints = sprintEntities.map { it.toSprint() }
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                sprints = sprints,
                                filteredSprints = sprints
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // On exception, try to load from local database
                sprintRepository.getSprintsByWorkspaceId(workspaceId).collect { sprintEntities ->
                    val sprints = sprintEntities.map { it.toSprint() }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            sprints = sprints,
                            filteredSprints = sprints,
                            error = "Could not connect to server. Showing cached data."
                        )
                    }
                }
            }
        }
    }

    fun createSprint(
        name: String,
        goal: String?,
        workspaceId: String,
        startDate: Date,
        endDate: Date,
        status: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isCreationSuccessful = false) }
            
            try {
                val userId = sessionManager.getUserId() ?: throw IllegalStateException("User not logged in")
                
                val response = sprintRepository.createSprint(
                    name = name,
                    description = "",
                    workspaceId = workspaceId,
                    startDate = startDate,
                    endDate = endDate,
                    goal = goal,
                    status = status
                )
                
                if (response.success && response.data != null) {
                    // Update UI state with the new sprint
                    val updatedSprints = _uiState.value.sprints.toMutableList()
                    updatedSprints.add(response.data)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            sprints = updatedSprints,
                            filteredSprints = updatedSprints.filter { sprint -> 
                                sprint.workspace_id == _uiState.value.currentWorkspaceId 
                            },
                            isCreationSuccessful = true,
                            selectedSprint = response.data
                        )
                    }
                    
                    // Refresh sprints list for the current workspace
                    _uiState.value.currentWorkspaceId?.let { workspaceId ->
                        loadSprints(workspaceId)
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to create sprint"
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

    fun selectSprint(sprint: Sprint) {
        _uiState.update { it.copy(selectedSprint = sprint) }
    }
    
    fun filterSprintsByStatus(status: String?) {
        val allSprints = _uiState.value.sprints
        val filtered = if (status == null || status == "All") {
            allSprints
        } else {
            allSprints.filter { it.status == status }
        }
        
        _uiState.update { it.copy(filteredSprints = filtered) }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedSprint = null) }
    }
    
    fun resetCreationState() {
        _uiState.update { it.copy(isCreationSuccessful = false, error = null) }
    }
} 