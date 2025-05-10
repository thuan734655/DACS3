package com.example.dacs3.ui.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.EpicDao
import com.example.dacs3.data.local.Status
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import com.example.dacs3.data.local.UserDao
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val epicDao: EpicDao,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _epicId = MutableStateFlow<String?>(null)
    val epicId: StateFlow<String?> = _epicId.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _epicName = MutableStateFlow<String?>(null)
    val epicName: StateFlow<String?> = _epicName.asStateFlow()
    
    fun setEpicId(id: String) {
        _epicId.value = id
        loadEpicName(id)
    }
    
    private fun loadEpicName(epicId: String) {
        viewModelScope.launch {
            try {
                val epic = epicDao.getEpicById(epicId)
                _epicName.value = epic?.name
            } catch (e: Exception) {
                Log.e("CreateTaskViewModel", "Error loading epic name: ${e.message}")
            }
        }
    }
    
    fun createTask(
        name: String,
        description: String,
        priority: Int,
        onComplete: (String) -> Unit
    ) {
        val epicId = _epicId.value ?: return
        val currentUserId = sessionManager.getUserId() ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Verify the user exists first
                val currentUser = userDao.getUserById(currentUserId)
                if (currentUser == null) {
                    _error.value = "User not found. Please log in again."
                    _isLoading.value = false
                    return@launch
                }
                
                // Verify the epic exists
                val epic = epicDao.getEpicById(epicId)
                if (epic == null) {
                    _error.value = "Epic not found."
                    _isLoading.value = false
                    return@launch
                }
                
                Log.d("CreateTask", "Creating task in epic ID: $epicId by user: $currentUserId")
                
                // Create a new task
                val taskId = UUID.randomUUID().toString()
                
                val task = TaskEntity(
                    taskId = taskId,
                    name = name,
                    description = description,
                    createdBy = currentUserId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    priority = priority,
                    status = Status.TO_DO,
                    progress = 0,
                    assignedToUserId = currentUserId, // Assign to creator by default
                    epicId = epicId
                )
                
                taskDao.insertTask(task)
                
                _isLoading.value = false
                onComplete(taskId)
            } catch (e: Exception) {
                Log.e("CreateTask", "Error creating task", e)
                _error.value = "Failed to create task: ${e.message}"
                _isLoading.value = false
            }
        }
    }
} 