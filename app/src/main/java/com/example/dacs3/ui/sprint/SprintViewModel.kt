package com.example.dacs3.ui.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.model.Task
import com.example.dacs3.data.repository.SprintRepository
import com.example.dacs3.data.repository.TaskRepository
import com.example.dacs3.util.WorkspacePreferences
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
    val sprintTasks: Map<String, List<Task>> = emptyMap(),
    val expandedSprintIds: Set<String> = emptySet(),
    val error: String? = null,
    val workspaceId: String = "",
    val isCreationSuccessful: Boolean = false,
    val isUpdateSuccessful: Boolean = false,
    val isDeletionSuccessful: Boolean = false,
    val activeSprintCount: Int = 0
)

@HiltViewModel
class SprintViewModel @Inject constructor(
    private val sprintRepository: SprintRepository,
    private val taskRepository: TaskRepository,
    private val workspacePreferences: WorkspacePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SprintUiState())
    val uiState: StateFlow<SprintUiState> = _uiState.asStateFlow()

    fun setWorkspaceId(workspaceId: String) {
        android.util.Log.d("SprintViewModel", "setWorkspaceId CALLED with workspaceId=$workspaceId")
        // Save the new workspaceId to preferences (in case it's coming from route params)
        workspacePreferences.saveSelectedWorkspaceId(workspaceId)
        
        // Clear existing sprints and reset state before loading new workspace data
        _uiState.update { 
            it.copy(
                workspaceId = workspaceId,
                sprints = emptyList(),
                sprintTasks = emptyMap(),
                expandedSprintIds = emptySet(),
                error = null,
                isLoading = true
            ) 
        }
        loadSprints(workspaceId)
    }
    
    /**
     * Get the workspaceId stored in preferences
     * If not found, returns empty string
     */
    fun getSavedWorkspaceId(): String {
        val savedId = workspacePreferences.getSelectedWorkspaceId()
        android.util.Log.d("SprintViewModel", "getSavedWorkspaceId returning: $savedId")
        return savedId
    }

    fun loadSprints(workspaceId: String) {
        android.util.Log.d("SprintViewModel", "loadSprints CALLED with workspaceId=$workspaceId")
        viewModelScope.launch {
            // Set loading state but don't reset sprints again (already handled in setWorkspaceId)
            if (!_uiState.value.isLoading) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            
            try {
                val response = sprintRepository.getAllSprintsFromApi(
                    workspaceId = workspaceId
                )
                android.util.Log.d("SprintViewModel", "API response: success=${response.success}, count=${response.count}, total=${response.total}, data=${response.data.map { it._id + ":" + it.name }}")
                
                if (response.success) {
                    // Clear any existing sprints and sprint tasks first to prevent mixing data
                    // from different workspaces
                    _uiState.update { 
                        it.copy(
                            sprints = response.data,
                            sprintTasks = emptyMap(),  // Clear all tasks for previous sprints
                            isLoading = false
                        )
                    }
                    android.util.Log.d("SprintViewModel", "Updated sprints in state: ${response.data.map { it._id + ":" + it.name }}")
                    
                    // Tải task cho mỗi sprint mới
                    response.data.forEach { sprint ->
                        loadTasksForSprint(sprint._id)
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Không thể tải danh sách sprint",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SprintViewModel", "Exception in loadSprints: ${e.message}")
                _uiState.update { 
                    it.copy(
                        error = "Lỗi: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun loadTasksForSprint(sprintId: String) {
        viewModelScope.launch {
            try {
                val response = taskRepository.getAllTasksFromApi(
                    sprintId = sprintId
                )
                
                if (response.success) {
                    _uiState.update { currentState ->
                        val updatedSprintTasks = currentState.sprintTasks.toMutableMap()
                        updatedSprintTasks[sprintId] = response.data
                        currentState.copy(sprintTasks = updatedSprintTasks)
                    }
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }
    }
    
    fun toggleSprintExpansion(sprintId: String) {
        _uiState.update { currentState ->
            val updatedExpandedIds = currentState.expandedSprintIds.toMutableSet()
            if (updatedExpandedIds.contains(sprintId)) {
                updatedExpandedIds.remove(sprintId)
            } else {
                updatedExpandedIds.add(sprintId)
            }
            currentState.copy(expandedSprintIds = updatedExpandedIds)
        }
    }
    
    fun createSprint(
        name: String,
        description: String?,
        startDate: Date,
        endDate: Date,
        goal: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = sprintRepository.createSprint(
                    name = name,
                    description = description,
                    workspaceId = _uiState.value.workspaceId,
                    startDate = startDate,
                    endDate = endDate,
                    goal = goal,
                    status = "TO_DO"
                )
                
                if (response.success && response.data != null) {
                    // Thêm sprint mới vào danh sách
                    val updatedSprints = _uiState.value.sprints.toMutableList()
                    updatedSprints.add(response.data)
                    
                    _uiState.update { 
                        it.copy(
                            sprints = updatedSprints,
                            isLoading = false,
                            isCreationSuccessful = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Không thể tạo sprint mới",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Lỗi: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun resetCreationState() {
        _uiState.update { it.copy(isCreationSuccessful = false) }
    }
    
    fun resetUpdateState() {
        _uiState.update { it.copy(isUpdateSuccessful = false) }
    }
    
    fun resetDeletionState() {
        _uiState.update { it.copy(isDeletionSuccessful = false) }
    }
    
    fun updateSprintStatus(sprintId: String, status: String) {
        android.util.Log.d("SprintViewModel", "updateSprintStatus CALLED with sprintId=$sprintId, status=$status")
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                android.util.Log.d("SprintViewModel", "Calling repository.updateSprint with status=$status")
                
                val response = sprintRepository.updateSprint(
                    id = sprintId,
                    status = status,
                    name = null,
                    description = null,
                    startDate = null,
                    endDate = null,
                    goal = null
                )
                
                android.util.Log.d("SprintViewModel", "API response: success=${response.success}, data=${response.data?._id}:${response.data?.name}")
                
                if (response.success && response.data != null) {
                    // Cập nhật sprint trong danh sách
                    val updatedSprints = _uiState.value.sprints.map { 
                        if (it._id == sprintId) response.data else it 
                    }
                    
                    android.util.Log.d("SprintViewModel", "Updating UI state with new sprint status")
                    _uiState.update { 
                        it.copy(
                            sprints = updatedSprints,
                            isLoading = false,
                            isUpdateSuccessful = true
                        )
                    }
                    android.util.Log.d("SprintViewModel", "isUpdateSuccessful set to true")
                } else {
                    android.util.Log.d("SprintViewModel", "API update failed")
                    _uiState.update { 
                        it.copy(
                            error = "Không thể cập nhật trạng thái sprint",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SprintViewModel", "Exception updating sprint status", e)
                _uiState.update { 
                    it.copy(
                        error = "Lỗi cập nhật trạng thái sprint: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteSprint(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val success = sprintRepository.deleteSprintFromApi(sprintId)
                
                if (success) {
                    // Xóa sprint khỏi danh sách
                    val updatedSprints = _uiState.value.sprints.filter { it._id != sprintId }
                    // Xóa tasks của sprint
                    val updatedSprintTasks = _uiState.value.sprintTasks.toMutableMap()
                    updatedSprintTasks.remove(sprintId)
                    
                    _uiState.update { 
                        it.copy(
                            sprints = updatedSprints,
                            sprintTasks = updatedSprintTasks,
                            isLoading = false,
                            isDeletionSuccessful = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Không thể xóa sprint",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Lỗi xóa sprint: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateSprint(
        id: String,
        name: String,
        description: String,
        startDate: Date,
        endDate: Date,
        goal: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
                
            try {
                val response = sprintRepository.updateSprint(
                    id = id,
                    name = name,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    goal = goal,
                    status = null
                )
                        
                if (response.success && response.data != null) {
                    // Cập nhật sprint trong danh sách
                    val updatedSprints = _uiState.value.sprints.map { 
                        if (it._id == id) response.data else it 
                    }
                            
                    _uiState.update { 
                        it.copy(
                            sprints = updatedSprints,
                            isLoading = false,
                            isUpdateSuccessful = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Không thể cập nhật sprint",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Lỗi: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadSprintDetail(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
                
            try {
                // Tải thông tin chi tiết của sprint
                val response = sprintRepository.getSprintByIdFromApi(sprintId)
                        
                if (response.success && response.data != null) {
                    // Cập nhật sprint trong danh sách nếu đã có
                    val updatedSprints = _uiState.value.sprints.toMutableList()
                    val existingIndex = updatedSprints.indexOfFirst { it._id == sprintId }
                        
                    if (existingIndex >= 0) {
                        updatedSprints[existingIndex] = response.data
                    } else {
                        updatedSprints.add(response.data)
                    }
                        
                    _uiState.update { 
                        it.copy(
                            sprints = updatedSprints,
                            isLoading = false
                        )
                    }
                    
                    // Tải danh sách task của sprint
                    loadTasksForSprint(sprintId)
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Không thể tải thông tin sprint",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Lỗi: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

// Hàm để thêm task vào sprint
    fun addTaskToSprint(sprintId: String, taskId: String) {
        viewModelScope.launch {
            try {
                val response = sprintRepository.addItemsToSprint(sprintId, listOf(taskId))
                if (response.success && response.data != null) {
                    // Cập nhật sprint
                    val updatedSprints = _uiState.value.sprints.map { 
                        if (it._id == sprintId) response.data else it 
                    }
                            
                    _uiState.update { 
                        it.copy(sprints = updatedSprints)
                    }
                            
                    // Tải lại tasks
                    loadTasksForSprint(sprintId)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Lỗi thêm task vào sprint: ${e.message}")
                }
            }
        }
    }

    // Hàm để xóa task khỏi sprint
    fun removeTaskFromSprint(sprintId: String, taskId: String) {
        viewModelScope.launch {
            try {
                val response = sprintRepository.removeItemsFromSprint(sprintId, listOf(taskId))
                if (response.success && response.data != null) {
                    // Cập nhật sprint
                    val updatedSprints = _uiState.value.sprints.map { 
                        if (it._id == sprintId) response.data else it 
                    }
                            
                    // Cập nhật danh sách tasks
                    val updatedTasks = _uiState.value.sprintTasks[sprintId]?.filter { it._id != taskId } ?: emptyList()
                    val updatedSprintTasks = _uiState.value.sprintTasks.toMutableMap()
                    updatedSprintTasks[sprintId] = updatedTasks
                            
                    _uiState.update { 
                        it.copy(
                            sprints = updatedSprints,
                            sprintTasks = updatedSprintTasks
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Lỗi xóa task khỏi sprint: ${e.message}")
                }
            }
        }
    }
}
