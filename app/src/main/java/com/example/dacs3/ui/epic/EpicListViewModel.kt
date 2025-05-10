package com.example.dacs3.ui.epic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.EpicDao
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.WorkspaceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpicListViewModel @Inject constructor(
    private val epicDao: EpicDao,
    private val workspaceDao: WorkspaceDao
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    
    private val _epics = MutableStateFlow<List<EpicEntity>>(emptyList())
    val epics: StateFlow<List<EpicEntity>> = _epics.asStateFlow()
    
    private val _workspaceName = MutableStateFlow<String?>(null)
    val workspaceName: StateFlow<String?> = _workspaceName.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun setWorkspaceId(id: String) {
        _workspaceId.value = id
        loadWorkspaceName(id)
        loadEpics(id)
    }
    
    private fun loadWorkspaceName(workspaceId: String) {
        viewModelScope.launch {
            try {
                val workspace = workspaceDao.getWorkspaceById(workspaceId)
                _workspaceName.value = workspace?.name
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("EpicListViewModel", "Workspace name loading job was cancelled")
                } else {
                    Log.e("EpicListViewModel", "Error loading workspace name: ${e.message}")
                }
            }
        }
    }
    
    private fun loadEpics(workspaceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                epicDao.getEpicsByWorkspace(workspaceId)
                    .catch { e ->
                        if (e is CancellationException) {
                            // Job cancellation is expected during view lifecycle changes, no need to show error
                            Log.d("EpicListViewModel", "Epics loading job was cancelled")
                        } else {
                            Log.e("EpicListViewModel", "Error loading epics: ${e.message}")
                            _error.value = "Failed to load epics: ${e.message}"
                        }
                    }
                    .collect { epicList ->
                        _epics.value = epicList
                        _error.value = null
                    }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("EpicListViewModel", "Epics collection job was cancelled")
                } else {
                    Log.e("EpicListViewModel", "Error collecting epics: ${e.message}")
                    _error.value = "Failed to load epics: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
} 