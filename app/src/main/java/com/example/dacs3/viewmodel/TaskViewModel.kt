package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Task
import com.example.dacs3.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getAllTasks(
        workspaceId: String? = null,
        sprintId: String? = null,
        epicId: String? = null,
        assigneeId: String? = null,
        status: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getAllTasks(workspaceId, sprintId, epicId, assigneeId, status)
                if (response.isSuccessful && response.body()?.success == true) {
                    _tasks.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getTaskById(taskId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getTaskById(taskId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _currentTask.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.createTask(task)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh tasks list after creation
                    getAllTasks(task.workspaceId)
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTask(taskId: String, task: Task) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateTask(taskId, task)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current task if it's the one being edited
                    if (_currentTask.value?.id == taskId) {
                        _currentTask.value = response.body()?.data
                    }
                    // Refresh tasks list after update
                    getAllTasks(task.workspaceId)
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTask(taskId: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteTask(taskId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Clear current task if it's the one being deleted
                    if (_currentTask.value?.id == taskId) {
                        _currentTask.value = null
                    }
                    // Refresh tasks list after deletion
                    getAllTasks(workspaceId)
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTaskStatus(taskId: String, status: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateTaskStatus(taskId, status)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current task if it's the one being modified
                    if (_currentTask.value?.id == taskId) {
                        _currentTask.value = response.body()?.data
                    }
                    // Refresh tasks list after update
                    getAllTasks(workspaceId)
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTaskAssignee(taskId: String, assigneeId: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateTaskAssignee(taskId, assigneeId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current task if it's the one being modified
                    if (_currentTask.value?.id == taskId) {
                        _currentTask.value = response.body()?.data
                    }
                    // Refresh tasks list after update
                    getAllTasks(workspaceId)
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }
} 