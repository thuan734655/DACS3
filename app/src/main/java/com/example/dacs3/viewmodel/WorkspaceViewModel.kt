package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.models.Task
import com.example.dacs3.models.User
import com.example.dacs3.models.Workspace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkspaceState(
    val workspace: Workspace? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor() : ViewModel() {
    private val _workspaceState = MutableStateFlow(WorkspaceState())
    val workspaceState: StateFlow<WorkspaceState> = _workspaceState.asStateFlow()

    fun loadWorkspace(workspaceId: String) {
        viewModelScope.launch {
            _workspaceState.value = _workspaceState.value.copy(isLoading = true)
            try {
                // TODO: Implement workspace loading from repository
                _workspaceState.value = _workspaceState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _workspaceState.value = _workspaceState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load workspace"
                )
            }
        }
    }

    fun addMember(workspaceId: String, member: User) {
        viewModelScope.launch {
            try {
                val currentWorkspace = _workspaceState.value.workspace
                if (currentWorkspace != null) {
                    val updatedMembers = currentWorkspace.members + member
                    val updatedWorkspace = currentWorkspace.copy(members = updatedMembers)
                    _workspaceState.value = _workspaceState.value.copy(workspace = updatedWorkspace)
                }
            } catch (e: Exception) {
                _workspaceState.value = _workspaceState.value.copy(
                    error = e.message ?: "Failed to add member"
                )
            }
        }
    }

    fun addTask(workspaceId: String, task: Task) {
        viewModelScope.launch {
            try {
                val currentWorkspace = _workspaceState.value.workspace
                if (currentWorkspace != null) {
                    val updatedTasks = currentWorkspace.tasks + task
                    val updatedWorkspace = currentWorkspace.copy(tasks = updatedTasks)
                    _workspaceState.value = _workspaceState.value.copy(workspace = updatedWorkspace)
                }
            } catch (e: Exception) {
                _workspaceState.value = _workspaceState.value.copy(
                    error = e.message ?: "Failed to add task"
                )
            }
        }
    }
} 