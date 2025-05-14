package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: WorkspaceRepository
) : ViewModel() {

    private val _workspaces = MutableStateFlow<List<Workspace>>(emptyList())
    val workspaces: StateFlow<List<Workspace>> = _workspaces

    private val _currentWorkspace = MutableStateFlow<Workspace?>(null)
    val currentWorkspace: StateFlow<Workspace?> = _currentWorkspace

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getAllWorkspaces() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getAllWorkspaces()
                if (response.isSuccessful && response.body()?.success == true) {
                    _workspaces.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getWorkspaceById(workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getWorkspaceById(workspaceId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _currentWorkspace.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createWorkspace(workspace: Workspace) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.createWorkspace(workspace)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh workspaces list after creation
                    getAllWorkspaces()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateWorkspace(workspaceId: String, workspace: Workspace) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateWorkspace(workspaceId, workspace)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current workspace if it's the one being edited
                    if (_currentWorkspace.value?.id == workspaceId) {
                        _currentWorkspace.value = response.body()?.data
                    }
                    // Refresh workspaces list after update
                    getAllWorkspaces()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteWorkspace(workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteWorkspace(workspaceId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Clear current workspace if it's the one being deleted
                    if (_currentWorkspace.value?.id == workspaceId) {
                        _currentWorkspace.value = null
                    }
                    // Refresh workspaces list after deletion
                    getAllWorkspaces()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMember(workspaceId: String, memberId: String, role: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.addMember(workspaceId, memberId, role)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current workspace if it's the one being modified
                    if (_currentWorkspace.value?.id == workspaceId) {
                        _currentWorkspace.value = response.body()?.data
                    }
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun removeMember(workspaceId: String, memberId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.removeMember(workspaceId, memberId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current workspace if it's the one being modified
                    if (_currentWorkspace.value?.id == workspaceId) {
                        _currentWorkspace.value = response.body()?.data
                    }
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }
}