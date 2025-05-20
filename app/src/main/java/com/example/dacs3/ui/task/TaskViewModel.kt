package com.example.dacs3.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Task
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

/**
 * State container for Task UI
 */
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val selectedTask: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isTaskSaved: Boolean = false,
    val isTaskDeleted: Boolean = false
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val workspacePreferences: WorkspacePreferences
) : ViewModel() {
    
    private val TAG = "TaskViewModel"
    
    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    init {
        // Nếu có workspaceId được lưu, tự động tải tasks
        val savedWorkspaceId = workspacePreferences.getSelectedWorkspaceId()
        if (savedWorkspaceId.isNotEmpty()) {
            loadTasksByWorkspaceId(savedWorkspaceId)
        }
    }
    
    /**
     * Tải danh sách tasks theo workspace ID
     */
    fun loadTasksByWorkspaceId(workspaceId: String) {
        Log.d(TAG, "loadTasksByWorkspaceId called with workspaceId: $workspaceId")
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling taskRepository.getAllTasksFromApi with workspaceId: $workspaceId")
                val response = taskRepository.getAllTasksFromApi(workspaceId = workspaceId)
                Log.d(TAG, "API response received: success=${response.success}, count=${response.count}")
                
                if (response.success) {
                    Log.d(TAG, "Tasks loaded successfully: ${response.data.size} tasks")
                    _uiState.update { 
                        it.copy(
                            tasks = response.data,
                            isLoading = false
                        )
                    }
                } else {
                    Log.e(TAG, "API returned success=false")
                    _uiState.update { 
                        it.copy(
                            error = "Failed to load tasks",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tasks: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Tải task theo ID
     */
    fun loadTaskById(taskId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val response = taskRepository.getTaskById(taskId)
                
                if (response.success && response.data != null) {
                    _uiState.update { 
                        it.copy(
                            selectedTask = response.data,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Failed to load task details",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading task details", e)
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Tạo task mới
     */
    fun createTask(
        title: String,
        description: String?,
        workspaceId: String,
        epicId: String?,
        assignedTo: String?,
        status: String?,
        priority: String?,
        estimatedHours: Int?,
        dueDate: Date?
    ) {
        _uiState.update { it.copy(isLoading = true, error = null, isTaskSaved = false) }
        
        viewModelScope.launch {
            try {
                val response = taskRepository.createTask(
                    title = title,
                    description = description,
                    workspaceId = workspaceId,
                    epicId = epicId,
                    assignedTo = assignedTo,
                    status = status ?: "TO_DO",
                    priority = priority ?: "MEDIUM",
                    estimatedHours = estimatedHours,
                    spentHours = 0,
                    startDate = Date(),
                    dueDate = dueDate,
                    sprintId = null
                )
                
                if (response.success && response.data != null) {
                    // Thêm task mới vào danh sách
                    val updatedTasks = _uiState.value.tasks.toMutableList()
                    updatedTasks.add(response.data)
                    
                    _uiState.update { 
                        it.copy(
                            tasks = updatedTasks,
                            isLoading = false,
                            isTaskSaved = true,
                            selectedTask = response.data
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Failed to create task",
                            isLoading = false,
                            isTaskSaved = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating task", e)
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false,
                        isTaskSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Cập nhật task
     */
    fun updateTask(
        id: String,
        title: String?,
        description: String?,
        epicId: String?,
        assignedTo: String?,
        status: String?,
        priority: String?,
        estimatedHours: Int?,
        spentHours: Int?,
        dueDate: Date?
    ) {
        _uiState.update { it.copy(isLoading = true, error = null, isTaskSaved = false) }
        
        viewModelScope.launch {
            try {
                val response = taskRepository.updateTask(
                    id = id,
                    title = title,
                    description = description,
                    epicId = epicId,
                    assignedTo = assignedTo,
                    status = status,
                    priority = priority,
                    estimatedHours = estimatedHours,
                    spentHours = spentHours,
                    startDate = null, // Không thay đổi start date hiện tại
                    dueDate = dueDate,
                    completedDate = if (status == "DONE") Date() else null,
                    sprintId = null // Không thay đổi sprint ID hiện tại
                )
                
                if (response.success && response.data != null) {
                    // Cập nhật task trong danh sách
                    val updatedTasks = _uiState.value.tasks.map { 
                        if (it._id == id) response.data else it 
                    }
                    
                    _uiState.update { 
                        it.copy(
                            tasks = updatedTasks,
                            selectedTask = response.data,
                            isLoading = false,
                            isTaskSaved = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Failed to update task",
                            isLoading = false,
                            isTaskSaved = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task", e)
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false,
                        isTaskSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Xóa task
     */
    fun deleteTask(id: String) {
        _uiState.update { it.copy(isLoading = true, error = null, isTaskDeleted = false) }
        
        viewModelScope.launch {
            try {
                val isDeleted = taskRepository.deleteTaskFromApi(id)
                
                if (isDeleted) {
                    // Xóa task khỏi danh sách
                    val updatedTasks = _uiState.value.tasks.filter { it._id != id }
                    
                    _uiState.update { 
                        it.copy(
                            tasks = updatedTasks,
                            selectedTask = null,
                            isLoading = false,
                            isTaskDeleted = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Failed to delete task",
                            isLoading = false,
                            isTaskDeleted = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task", e)
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false,
                        isTaskDeleted = false
                    )
                }
            }
        }
    }
    
    /**
     * Thêm bình luận vào task
     */
    fun addComment(taskId: String, content: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val response = taskRepository.addComment(taskId, content)
                
                if (response.success && response.data != null) {
                    // Tải lại task để cập nhật danh sách comments
                    loadTaskById(taskId)
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Failed to add comment",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding comment", e)
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Reset trạng thái lưu task
     */
    fun resetSaveState() {
        _uiState.update { it.copy(isTaskSaved = false, isTaskDeleted = false) }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}