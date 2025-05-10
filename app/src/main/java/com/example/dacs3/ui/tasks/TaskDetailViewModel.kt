package com.example.dacs3.ui.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.BugDao
import com.example.dacs3.data.local.BugEntity
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import com.example.dacs3.data.local.UserDao
import com.example.dacs3.data.local.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val bugDao: BugDao,
    private val userDao: UserDao
) : ViewModel() {
    
    private val _taskId = MutableStateFlow<String?>(null)
    
    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> = _task.asStateFlow()
    
    private val _bugs = MutableStateFlow<List<BugEntity>>(emptyList())
    val bugs: StateFlow<List<BugEntity>> = _bugs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _assignedUser = MutableStateFlow<UserEntity?>(null)
    val assignedUser: StateFlow<UserEntity?> = _assignedUser.asStateFlow()
    
    private val _createdByUser = MutableStateFlow<UserEntity?>(null)
    val createdByUser: StateFlow<UserEntity?> = _createdByUser.asStateFlow()
    
    fun setTaskId(id: String) {
        _taskId.value = id
        loadTask(id)
        loadBugs(id)
    }
    
    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val task = taskDao.getTaskById(taskId)
                _task.value = task
                
                // Load assigned user and creator
                task?.let { loadAssociatedUsers(it) }
                
                _error.value = null
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error loading task: ${e.message}")
                _error.value = "Failed to load task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun loadAssociatedUsers(task: TaskEntity) {
        try {
            // Load assigned user if available
            task.assignedToUserId?.let { userId ->
                _assignedUser.value = userDao.getUserById(userId)
            }
            
            // Load creator
            _createdByUser.value = userDao.getUserById(task.createdBy)
        } catch (e: Exception) {
            Log.e("TaskDetailViewModel", "Error loading associated users: ${e.message}")
        }
    }
    
    private fun loadBugs(taskId: String) {
        viewModelScope.launch {
            try {
                bugDao.getBugsByTaskId(taskId)
                    .catch { e ->
                        Log.e("TaskDetailViewModel", "Error loading bugs: ${e.message}")
                        _error.value = "Failed to load bugs: ${e.message}"
                    }
                    .collect { bugList ->
                        _bugs.value = bugList
                    }
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error collecting bugs: ${e.message}")
                _error.value = "Failed to load bugs: ${e.message}"
            }
        }
    }
    
    fun updateTaskProgress(progress: Int) {
        val currentTask = _task.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedTask = currentTask.copy(progress = progress)
                taskDao.updateTask(updatedTask)
                _task.value = updatedTask
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error updating task progress: ${e.message}")
                _error.value = "Failed to update progress: ${e.message}"
            }
        }
    }
    
    fun updateTaskStatus(status: com.example.dacs3.data.local.Status) {
        val currentTask = _task.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedTask = currentTask.copy(status = status)
                taskDao.updateTask(updatedTask)
                _task.value = updatedTask
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error updating task status: ${e.message}")
                _error.value = "Failed to update status: ${e.message}"
            }
        }
    }
} 