package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.MockData
import com.example.dacs3.models.Task
import com.example.dacs3.models.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor() : ViewModel() {
    private val _taskState = MutableStateFlow(TaskState())
    val taskState: StateFlow<TaskState> = _taskState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _taskState.value = _taskState.value.copy(isLoading = true)
            try {
                // In a real app, this would be an API call
                val task = MockData.mockHomeResponse.tasks.find { it.id == taskId }
                _taskState.value = TaskState(task = task)
            } catch (e: Exception) {
                _taskState.value = _taskState.value.copy(error = e.message)
            } finally {
                _taskState.value = _taskState.value.copy(isLoading = false)
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                // In a real app, this would be an API call
                val task = _taskState.value.task?.copy(status = newStatus)
                _taskState.value = _taskState.value.copy(task = task)
            } catch (e: Exception) {
                _taskState.value = _taskState.value.copy(error = e.message)
            }
        }
    }
} 