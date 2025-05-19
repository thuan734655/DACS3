package com.example.dacs3.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.entity.WorkspaceEntity
import com.example.dacs3.data.model.CreateWorkspaceRequest
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
) : ViewModel() {

    private val _workspaces = MutableStateFlow<List<WorkspaceEntity>>(emptyList())
    val workspaces: StateFlow<List<WorkspaceEntity>> = _workspaces

    private val _selectedWorkspace = MutableStateFlow<WorkspaceEntity?>(null)
    val selectedWorkspace: StateFlow<WorkspaceEntity?> = _selectedWorkspace

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private val PAGE_SIZE = 10

    init {
        fetchWorkspaces(true)
    }

    fun fetchWorkspaces(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // First, get data from local database (offline-first approach)
                workspaceRepository.getAll()
                    .catch { e ->
                        _error.value = e.message
                        _isLoading.value = false
                    }
                    .collectLatest { localWorkspaces ->
                        _workspaces.value = localWorkspaces
                        
                        // Select first workspace if none is selected
                        if (_selectedWorkspace.value == null && localWorkspaces.isNotEmpty()) {
                            _selectedWorkspace.value = localWorkspaces.first()
                        }
                        
                        // Then, if online or forced refresh, sync with remote
                        if (forceRefresh) {
                            syncWithRemote()
                        } else {
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                _error.value = "Error fetching workspaces: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private suspend fun syncWithRemote() {
        try {
            val response = workspaceRepository.getAllWorkspacesFromApi(
                page = _currentPage.value,
                limit = PAGE_SIZE
            )
            
            if (response.success) {
                _hasMoreData.value = response.data.size >= PAGE_SIZE
                // Clear error if response is successful (even if empty)
                _error.value = null
            } else {
                _error.value = "Failed to sync with server"
            }
        } catch (e: Exception) {
            _error.value = "Network error: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun loadMoreWorkspaces() {
        if (!_isLoading.value && _hasMoreData.value) {
            _currentPage.value = _currentPage.value + 1
            viewModelScope.launch {
                syncWithRemote()
            }
        }
    }

    fun selectWorkspace(workspace: WorkspaceEntity) {
        _selectedWorkspace.value = workspace
    }

    fun createWorkspace(name: String, description: String?) {
        viewModelScope.launch {
            val response = workspaceRepository.createWorkspace(name, description)
        }
    }

    fun refreshWorkspaces() {
        _currentPage.value = 1
        fetchWorkspaces(true)
    }

    fun clearError() {
        _error.value = null
    }
} 