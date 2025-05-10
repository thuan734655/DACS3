package com.example.dacs3.ui.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.EpicDao
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val epicDao: EpicDao
) : ViewModel() {
    
    private val _epicId = MutableStateFlow<String?>(null)
    
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()
    
    private val _epic = MutableStateFlow<EpicEntity?>(null)
    val epic: StateFlow<EpicEntity?> = _epic.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun setEpicId(id: String) {
        _epicId.value = id
        loadEpic(id)
        loadTasks(id)
    }
    
    private fun loadEpic(epicId: String) {
        viewModelScope.launch {
            try {
                val epic = epicDao.getEpicById(epicId)
                _epic.value = epic
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("TaskListViewModel", "Epic loading job was cancelled")
                } else {
                    Log.e("TaskListViewModel", "Error loading epic: ${e.message}")
                }
            }
        }
    }
    
    private fun loadTasks(epicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                taskDao.getTasksByEpic(epicId)
                    .catch { e ->
                        if (e is CancellationException) {
                            // Job cancellation is expected during view lifecycle changes, no need to show error
                            Log.d("TaskListViewModel", "Tasks loading job was cancelled")
                        } else {
                            Log.e("TaskListViewModel", "Error loading tasks: ${e.message}")
                            _error.value = "Failed to load tasks: ${e.message}"
                        }
                    }
                    .collect { taskList ->
                        _tasks.value = taskList
                        _error.value = null
                    }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("TaskListViewModel", "Tasks collection job was cancelled")
                } else {
                    Log.e("TaskListViewModel", "Error collecting tasks: ${e.message}")
                    _error.value = "Failed to load tasks: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
} 