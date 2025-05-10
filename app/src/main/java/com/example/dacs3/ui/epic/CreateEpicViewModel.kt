package com.example.dacs3.ui.epic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.EpicDao
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.Status
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
class CreateEpicViewModel @Inject constructor(
    private val epicDao: EpicDao,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    val workspaceId: StateFlow<String?> = _workspaceId.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun setWorkspaceId(id: String) {
        _workspaceId.value = id
    }
    
    fun createEpic(
        name: String,
        description: String,
        priority: Int,
        onComplete: (String) -> Unit
    ) {
        val workspaceId = _workspaceId.value ?: return
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
                
                Log.d("CreateEpic", "Creating epic with workspace ID: $workspaceId by user: $currentUserId")
                
                // Create a new epic
                val epicId = UUID.randomUUID().toString()
                
                val epic = EpicEntity(
                    epicId = epicId,
                    name = name,
                    description = description,
                    createdBy = currentUserId,
                    priority = priority,
                    status = Status.TO_DO,
                    workspaceId = workspaceId
                )
                
                epicDao.insertEpic(epic)
                
                _isLoading.value = false
                onComplete(epicId)
            } catch (e: Exception) {
                Log.e("CreateEpic", "Error creating epic", e)
                _error.value = "Failed to create epic: ${e.message}"
                _isLoading.value = false
            }
        }
    }
} 