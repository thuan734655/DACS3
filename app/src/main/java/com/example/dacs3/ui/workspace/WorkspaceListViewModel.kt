package com.example.dacs3.ui.workspace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.WorkspaceDao
import com.example.dacs3.data.local.WorkspaceEntity
import com.example.dacs3.data.local.WorkspaceUserMembership
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkspaceListViewModel @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val repository: WorkspaceRepository
) : ViewModel() {
    
    val workspaces = workspaceDao.getAllWorkspaces()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        viewModelScope.launch {
            // We no longer need to create sample workspaces
            // Each user should create their own workspaces
        }
    }
    
    fun createWorkspace(name: String, description: String, currentUserId: String): String {
        val workspaceId = UUID.randomUUID().toString()
        
        viewModelScope.launch {
            try {
                Log.d("Workspace", "Creating workspace with ID: $workspaceId, created by: $currentUserId")
                
                // Verify user exists
                val user = repository.getUserById(currentUserId)
                if (user == null) {
                    Log.e("Workspace", "User with ID $currentUserId not found")
                    throw IllegalStateException("User not found")
                }
                
                // Create workspace entity
                val newWorkspace = WorkspaceEntity(
                    workspaceId = workspaceId,
                    name = name,
                    description = description,
                    createdBy = currentUserId,
                    leaderId = currentUserId
                )
                
                // Create workspace membership (the creator becomes a member automatically)
                val membership = WorkspaceUserMembership(
                    userId = currentUserId,
                    workspaceId = workspaceId,
                    role = "ADMIN", // Creator is admin by default
                    joinedAt = System.currentTimeMillis()
                )
                
                // Use transaction to ensure data consistency
                repository.createWorkspaceWithMembership(newWorkspace, membership)
                
                Log.d("Workspace", "Workspace created successfully")
            } catch (e: Exception) {
                Log.e("Workspace", "Error creating workspace", e)
                throw e
            }
        }
        
        return workspaceId
    }
} 