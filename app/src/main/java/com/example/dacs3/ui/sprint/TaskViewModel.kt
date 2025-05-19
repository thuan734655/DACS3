package com.example.dacs3.ui.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Task
import com.example.dacs3.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun getTasksBySprintId(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Lấy danh sách task từ API
                val response = taskRepository.getAllTasksFromApi(sprintId = sprintId)
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            tasks = response.data ?: emptyList()
                        )
                    }
                } else {
                    // Nếu API thất bại, sử dụng dữ liệu từ local database
                    taskRepository.getTasksBySprintId(sprintId).collect { taskEntities ->
                        val tasks = taskEntities.map { it.toTask() }
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                tasks = tasks
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ
                taskRepository.getTasksBySprintId(sprintId).collect { taskEntities ->
                    val tasks = taskEntities.map { it.toTask() }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            tasks = tasks,
                            error = "Could not connect to server. Showing cached data."
                        )
                    }
                }
            }
        }
    }
}