package com.example.dacs3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WorkspaceState {
    object Loading : WorkspaceState()
    data class Success<T>(val data: T) : WorkspaceState()
    data class Error(val message: String) : WorkspaceState()
}

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: WorkspaceRepository
) : ViewModel() {

    private val _workspacesState = MutableStateFlow<WorkspaceState>(WorkspaceState.Loading)
    val workspacesState: StateFlow<WorkspaceState> = _workspacesState

    private val _workspaceState = MutableStateFlow<WorkspaceState>(WorkspaceState.Loading)
    val workspaceState: StateFlow<WorkspaceState> = _workspaceState

    private val _operationState = MutableStateFlow<WorkspaceState>(WorkspaceState.Loading)
    val operationState: StateFlow<WorkspaceState> = _operationState

    fun getAllWorkspaces() {
        viewModelScope.launch {
            _workspacesState.value = WorkspaceState.Loading
            try {
                val response = repository.getAllWorkspaces()
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _workspacesState.value = WorkspaceState.Success(apiResponse.data)
                    } ?: run {
                        _workspacesState.value = WorkspaceState.Error("Empty response")
                    }
                } else {
                    _workspacesState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _workspacesState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getWorkspaceById(workspaceId: String) {
        viewModelScope.launch {
            _workspaceState.value = WorkspaceState.Loading
            try {
                val response = repository.getWorkspaceById(workspaceId)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _workspaceState.value = WorkspaceState.Success(apiResponse.data)
                    } ?: run {
                        _workspaceState.value = WorkspaceState.Error("Empty response")
                    }
                } else {
                    _workspaceState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _workspaceState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createWorkspace(workspace: Workspace) {
        viewModelScope.launch {
            _operationState.value = WorkspaceState.Loading
            try {
                val response = repository.createWorkspace(workspace)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _operationState.value = WorkspaceState.Success(apiResponse.data)
                    } ?: run {
                        _operationState.value = WorkspaceState.Error("Empty response")
                    }
                } else {
                    _operationState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _operationState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateWorkspace(workspaceId: String, workspace: Workspace) {
        viewModelScope.launch {
            _operationState.value = WorkspaceState.Loading
            try {
                val response = repository.updateWorkspace(workspaceId, workspace)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _operationState.value = WorkspaceState.Success(apiResponse.data)
                    } ?: run {
                        _operationState.value = WorkspaceState.Error("Empty response")
                    }
                } else {
                    _operationState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _operationState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteWorkspace(workspaceId: String) {
        viewModelScope.launch {
            _operationState.value = WorkspaceState.Loading
            try {
                val response = repository.deleteWorkspace(workspaceId)
                if (response.isSuccessful) {
                    _operationState.value = WorkspaceState.Success(true)
                } else {
                    _operationState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _operationState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMember(workspaceId: String, memberId: String, role: String) {
        viewModelScope.launch {
            _operationState.value = WorkspaceState.Loading
            try {
                val response = repository.addMember(workspaceId, memberId, role)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _operationState.value = WorkspaceState.Success(apiResponse.data)
                    } ?: run {
                        _operationState.value = WorkspaceState.Error("Empty response")
                    }
                } else {
                    _operationState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _operationState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun removeMember(workspaceId: String, memberId: String) {
        viewModelScope.launch {
            _operationState.value = WorkspaceState.Loading
            try {
                val response = repository.removeMember(workspaceId, memberId)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _operationState.value = WorkspaceState.Success(apiResponse.data)
                    } ?: run {
                        _operationState.value = WorkspaceState.Error("Empty response")
                    }
                } else {
                    _operationState.value = WorkspaceState.Error(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _operationState.value = WorkspaceState.Error(e.message ?: "Unknown error")
            }
        }
    }
} 