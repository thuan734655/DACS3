package com.example.dacs3.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.WorkspaceDao
import com.example.dacs3.data.local.WorkspaceEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkspaceListViewModel @Inject constructor(
    private val workspaceDao: WorkspaceDao
) : ViewModel() {
    
    val workspaces = workspaceDao.getAllWorkspaces()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Create sample workspaces if none exist
            val workspaceCount = workspaceDao.getWorkspaceCount()
            if (workspaceCount == 0) {
                createSampleWorkspaces()
            }
        }
    }
    
    private suspend fun createSampleWorkspaces() {
        _isLoading.value = true
        
        val sampleWorkspaces = listOf(
            WorkspaceEntity(
                workspaceId = "workspace1",
                name = "Development Team",
                description = "Main workspace for the software development team",
                createdBy = "user1",
                leaderId = "user1"
            ),
            WorkspaceEntity(
                workspaceId = "workspace2",
                name = "Design Team",
                description = "Workspace for the UI/UX design team",
                createdBy = "user2",
                leaderId = "user2"
            ),
            WorkspaceEntity(
                workspaceId = "workspace3",
                name = "Marketing",
                description = "Workspace for the marketing and sales team",
                createdBy = "user1",
                leaderId = "user3"
            )
        )
        
        workspaceDao.insertWorkspaces(sampleWorkspaces)
        _isLoading.value = false
    }
    
    fun createWorkspace(name: String, description: String, currentUserId: String): String {
        val workspaceId = UUID.randomUUID().toString()
        
        viewModelScope.launch {
            val newWorkspace = WorkspaceEntity(
                workspaceId = workspaceId,
                name = name,
                description = description,
                createdBy = currentUserId,
                leaderId = currentUserId
            )
            
            workspaceDao.insertWorkspace(newWorkspace)
        }
        
        return workspaceId
    }
} 