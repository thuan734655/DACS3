package com.example.dacs3.ui.bugs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.BugDao
import com.example.dacs3.data.local.BugEntity
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BugListViewModel @Inject constructor(
    private val bugDao: BugDao,
    private val taskDao: TaskDao
) : ViewModel() {
    
    private val _taskId = MutableStateFlow<String?>(null)
    
    private val _bugs = MutableStateFlow<List<BugEntity>>(emptyList())
    val bugs: StateFlow<List<BugEntity>> = _bugs.asStateFlow()
    
    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> = _task.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private var bugLoadingJob: Job? = null
    
    fun setTaskId(id: String) {
        if (_taskId.value == id && _bugs.value.isNotEmpty()) {
            // Already loaded data for this task and have data, don't reload
            return
        }
        
        _taskId.value = id
        loadTask(id)
        loadBugs(id)
    }
    
    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            try {
                val task = taskDao.getTaskById(taskId)
                _task.value = task
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("BugListViewModel", "Task loading job was cancelled")
                } else {
                    Log.e("BugListViewModel", "Error loading task: ${e.message}")
                }
            }
        }
    }
    
    private fun loadBugs(taskId: String) {
        // Cancel previous job if active
        bugLoadingJob?.cancel()
        
        bugLoadingJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // First try to get data directly for immediate display
                val initialBugs = bugDao.getBugsByTaskIdSync(taskId)
                if (initialBugs.isNotEmpty()) {
                    _bugs.value = initialBugs
                }
                
                // Then start observing for changes
                bugDao.getBugsByTaskId(taskId)
                    .catch { e ->
                        if (e is CancellationException) {
                            // Job cancellation is expected during view lifecycle changes, no need to show error
                            Log.d("BugListViewModel", "Bugs loading job was cancelled")
                        } else {
                            Log.e("BugListViewModel", "Error loading bugs: ${e.message}")
                            _error.value = "Failed to load bugs: ${e.message}"
                        }
                    }
                    .collectLatest { bugList ->
                        _bugs.value = bugList
                        _error.value = null
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("BugListViewModel", "Bugs collection job was cancelled")
                } else {
                    Log.e("BugListViewModel", "Error collecting bugs: ${e.message}")
                    _error.value = "Failed to load bugs: ${e.message}"
                    _isLoading.value = false
                }
            }
        }
    }
    
    override fun onCleared() {
        bugLoadingJob?.cancel()
        super.onCleared()
    }
} 