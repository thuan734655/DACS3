package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Epic
import com.example.dacs3.data.repository.EpicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class picViewModel @Inject constructor(
    private val repository: EpicRepository
) : ViewModel() {

    private val _epics = MutableStateFlow<List<Epic>>(emptyList())
    val epics: StateFlow<List<Epic>> = _epics

    private val _currentEpic = MutableStateFlow<Epic?>(null)
    val currentEpic: StateFlow<Epic?> = _currentEpic

    private val _epicTasks = MutableStateFlow<Any?>(null)
    val epicTasks: StateFlow<Any?> = _epicTasks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getAllEpics(workspaceId: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getAllEpics(workspaceId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _epics.value = response.body()?.data ?: emptyList()
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

    fun getEpicById(epicId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getEpicById(epicId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _currentEpic.value = response.body()?.data
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

    fun createEpic(epic: Epic) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.createEpic(epic)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh epics list after creation
                    getAllEpics(epic.workspaceId)
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

    fun updateEpic(epicId: String, epic: Epic) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateEpic(epicId, epic)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current epic if it's the one being edited
                    if (_currentEpic.value?.id == epicId) {
                        _currentEpic.value = response.body()?.data
                    }
                    // Refresh epics list after update
                    getAllEpics(epic.workspaceId)
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

    fun deleteEpic(epicId: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteEpic(epicId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Clear current epic if it's the one being deleted
                    if (_currentEpic.value?.id == epicId) {
                        _currentEpic.value = null
                    }
                    // Refresh epics list after deletion
                    getAllEpics(workspaceId)
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

    fun getEpicTasks(epicId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getEpicTasks(epicId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _epicTasks.value = response.body()?.data
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