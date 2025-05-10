package com.example.dacs3.ui.bugs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.BugDao
import com.example.dacs3.data.local.BugEntity
import com.example.dacs3.data.local.Status
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import com.example.dacs3.data.local.UserDao
import com.example.dacs3.data.local.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateBugViewModel @Inject constructor(
    private val bugDao: BugDao,
    private val taskDao: TaskDao,
    private val userDao: UserDao
) : ViewModel() {
    
    private val _taskId = MutableStateFlow<String?>(null)
    val taskId: StateFlow<String?> = _taskId.asStateFlow()
    
    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> = _task.asStateFlow()
    
    private val _bugName = MutableStateFlow("")
    val bugName: StateFlow<String> = _bugName.asStateFlow()
    
    private val _bugDescription = MutableStateFlow("")
    val bugDescription: StateFlow<String> = _bugDescription.asStateFlow()
    
    private val _priority = MutableStateFlow(3)
    val priority: StateFlow<Int> = _priority.asStateFlow()
    
    private val _status = MutableStateFlow(Status.TO_DO)
    val status: StateFlow<Status> = _status.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    fun setTaskId(id: String) {
        _taskId.value = id
        loadTask(id)
    }
    
    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            try {
                val task = taskDao.getTaskById(taskId)
                _task.value = task
            } catch (e: Exception) {
                Log.e("CreateBugViewModel", "Error loading task: ${e.message}")
            }
        }
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                // In a real app, this would come from a UserRepository or a SessionManager
                // For this example, we'll just get the first user
                val users = userDao.getAllUsersSync()
                if (users.isNotEmpty()) {
                    _currentUser.value = users.first()
                }
            } catch (e: Exception) {
                Log.e("CreateBugViewModel", "Error loading current user: ${e.message}")
            }
        }
    }
    
    fun updateBugName(name: String) {
        _bugName.value = name
    }
    
    fun updateBugDescription(description: String) {
        _bugDescription.value = description
    }
    
    fun updatePriority(priority: Int) {
        _priority.value = priority
    }
    
    fun updateStatus(status: Status) {
        _status.value = status
    }
    
    fun createBug() {
        val name = _bugName.value
        val description = _bugDescription.value
        val taskId = _taskId.value ?: return
        val currentUser = _currentUser.value ?: return
        
        // Validate input
        if (name.isBlank()) {
            _error.value = "Bug name cannot be empty"
            return
        }
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val newBug = BugEntity(
                    bugId = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    createdBy = currentUser.userId,
                    priority = _priority.value,
                    status = _status.value,
                    taskId = taskId
                )
                
                bugDao.insertBug(newBug)
                _isSuccess.value = true
            } catch (e: Exception) {
                Log.e("CreateBugViewModel", "Error creating bug: ${e.message}")
                _error.value = "Failed to create bug: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetSuccess() {
        _isSuccess.value = false
    }
} 