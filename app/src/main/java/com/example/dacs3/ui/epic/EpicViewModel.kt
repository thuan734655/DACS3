package com.example.dacs3.ui.epic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Epic
import com.example.dacs3.data.repository.EpicRepository
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class EpicUiState(
    val isLoading: Boolean = false,
    val epics: List<Epic> = emptyList(),
    val filteredEpics: List<Epic> = emptyList(),
    val error: String? = null,
    val isCreationSuccessful: Boolean = false,
    val isUpdateSuccessful: Boolean = false,
    val selectedEpic: Epic? = null,
    val currentWorkspaceId: String? = null
)

@HiltViewModel
class EpicViewModel @Inject constructor(
    private val epicRepository: EpicRepository,
    private val sessionManager: SessionManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EpicUiState())
    val uiState: StateFlow<EpicUiState> = _uiState.asStateFlow()

    fun loadEpics(workspaceId: String) {
        _uiState.update { it.copy(currentWorkspaceId = workspaceId) }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // First try to get from API
                val response = epicRepository.getAllEpicsFromApi(workspaceId = workspaceId)
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            epics = response.data ?: emptyList(),
                            filteredEpics = response.data ?: emptyList()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("EpicViewModel", "Error fetching epics from API: ${e.message}")
            }
        }
    }

    fun createEpic(
        title: String,
        description: String?,
        workspaceId: String,
        startDate: Date?,
        dueDate: Date?,
        priority: String,
        status: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isCreationSuccessful = false) }
            
            try {
                val userId = sessionManager.getUserId() ?: throw IllegalStateException("User not logged in")
                
                val response = epicRepository.createEpic(
                    title = title,
                    description = description,
                    workspaceId = workspaceId,
                    assignedTo = null,
                    status = status,
                    priority = priority,
                    startDate = startDate,
                    dueDate = dueDate,
                    sprintId = null
                )
                
                if (response.success && response.data != null) {
                    // Update UI state with the new epic
                    val updatedEpics = _uiState.value.epics.toMutableList()
                    updatedEpics.add(response.data)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            epics = updatedEpics,
                            filteredEpics = updatedEpics.filter { epic -> 
                                epic.workspace_id._id == _uiState.value.currentWorkspaceId 
                            },
                            isCreationSuccessful = true,
                            selectedEpic = response.data
                        )
                    }
                    
                    // Refresh epics list for the current workspace
                    _uiState.value.currentWorkspaceId?.let { workspaceId ->
                        loadEpics(workspaceId)
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to create epic"
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

    fun selectEpic(epic: Epic) {
        _uiState.update { it.copy(selectedEpic = epic) }
    }
    
    fun filterEpicsByStatus(status: String?) {
        val allEpics = _uiState.value.epics
        val filtered = if (status == null || status == "All") {
            allEpics
        } else {
            allEpics.filter { it.status == status }
        }
        
        _uiState.update { it.copy(filteredEpics = filtered) }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedEpic = null) }
    }
    
    fun resetCreationState() {
        _uiState.update { it.copy(isCreationSuccessful = false, error = null) }
    }
    
    fun resetUpdateState() {
        _uiState.update { it.copy(isUpdateSuccessful = false) }
    }
    
    fun deleteEpic(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = epicRepository.deleteEpic(id)
                
                if (response.success) {
                    // Remove the deleted epic from the list
                    val updatedEpics = _uiState.value.epics.filter { it._id != id }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            epics = updatedEpics,
                            filteredEpics = updatedEpics.filter { epic -> 
                                epic.workspace_id._id == _uiState.value.currentWorkspaceId 
                            }
                        )
                    }
                    
                    // Refresh epics list for the current workspace
                    _uiState.value.currentWorkspaceId?.let { workspaceId ->
                        loadEpics(workspaceId)
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to delete epic"
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
    
    fun updateEpic(
        id: String,
        title: String,
        description: String?,
        assignedTo: String?,
        status: String,
        priority: String,
        startDate: Date?,
        dueDate: Date?,
        completedDate: Date?,
        sprintId: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isUpdateSuccessful = false) }
            
            try {
                val response = epicRepository.updateEpic(
                    id = id,
                    title = title,
                    description = description,
                    assignedTo = assignedTo,
                    status = status,
                    priority = priority,
                    startDate = startDate,
                    dueDate = dueDate,
                    completedDate = completedDate,
                    sprintId = sprintId
                )
                
                if (response.success && response.data != null) {
                    // Update UI state with the updated epic
                    val updatedEpics = _uiState.value.epics.toMutableList()
                    val index = updatedEpics.indexOfFirst { it._id == id }
                    
                    if (index != -1) {
                        updatedEpics[index] = response.data
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            epics = updatedEpics,
                            filteredEpics = updatedEpics.filter { epic -> 
                                epic.workspace_id._id == _uiState.value.currentWorkspaceId 
                            },
                            isUpdateSuccessful = true,
                            selectedEpic = response.data
                        )
                    }
                    
                    // Refresh epics list for the current workspace
                    _uiState.value.currentWorkspaceId?.let { workspaceId ->
                        loadEpics(workspaceId)
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to update epic"
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