package com.example.dacs3.ui.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.repository.SprintRepository
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
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SprintUiState())
    val uiState: StateFlow<SprintUiState> = _uiState.asStateFlow()

    fun loadSprints(workspaceId: String) {
        _uiState.update { it.copy(currentWorkspaceId = workspaceId) }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = sprintRepository.getAllSprintsFromApi(workspaceId = workspaceId)
                if (response.success) {
                    val data = response.data.orEmpty()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sprints = data,
                            filteredSprints = data
                        )
                    }
                } else {
                    sprintRepository.getSprintsByWorkspaceId(workspaceId).collect { entities ->
                        val list = entities.map { it.toSprint() }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                sprints = list,
                                filteredSprints = list
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                sprintRepository.getSprintsByWorkspaceId(workspaceId).collect { entities ->
                    val list = entities.map { it.toSprint() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sprints = list,
                            filteredSprints = list,
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
                val userId = sessionManager.getUserId() ?: error("User not logged in")
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
                    // Build updated list and apply workspace filter correctly
                    val updatedList = _uiState.value.sprints.toMutableList().apply { add(response.data) }
                    val currentWs = _uiState.value.currentWorkspaceId
                    val filtered = if (currentWs != null) {
                        updatedList.filter { sprint -> sprint.workspace_id == currentWs }
                    } else updatedList

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sprints = updatedList,
                            filteredSprints = filtered,
                            isCreationSuccessful = true,
                            selectedSprint = response.data
                        )
                    }

                    // Reload sprints from source to ensure consistency
                    currentWs?.let { loadSprints(it) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to create sprint") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun selectSprint(sprint: Sprint) {
        _uiState.update { it.copy(selectedSprint = sprint) }
    }

    fun filterSprintsByStatus(status: String?) {
        val all = _uiState.value.sprints
        val filtered = when {
            status.isNullOrBlank() || status == "All" -> all
            else -> all.filter { it.status == status }
        }
        _uiState.update { it.copy(filteredSprints = filtered) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedSprint = null) }
    }

    fun resetCreationState() {
        _uiState.update { it.copy(isCreationSuccessful = false, error = null) }
    }

    fun getSprintById(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = sprintRepository.getSprintByIdFromApi(id)
                if (response.success && response.data != null) {
                    _uiState.update { it.copy(isLoading = false, selectedSprint = response.data) }
                } else {
                    val entity = sprintRepository.getById(id)
                    if (entity != null) {
                        _uiState.update {
                            it.copy(isLoading = false, selectedSprint = entity.toSprint())
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Could not find sprint") }
                    }
                }
            } catch (e: Exception) {
                val entity = sprintRepository.getById(id)
                if (entity != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedSprint = entity.toSprint(),
                            error = "Could not connect to server. Showing cached data."
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
                }
            }
        }
    }

    fun setError(errorMessage: String) {
        _uiState.update { it.copy(error = errorMessage) }
    }
}