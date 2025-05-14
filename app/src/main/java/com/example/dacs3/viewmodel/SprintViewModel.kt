package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Sprint
import com.example.dacs3.data.repository.SprintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SprintViewModel @Inject constructor(
    private val repository: SprintRepository
) : ViewModel() {

    private val _sprints = MutableStateFlow<List<Sprint>>(emptyList())
    val sprints: StateFlow<List<Sprint>> = _sprints

    private val _currentSprint = MutableStateFlow<Sprint?>(null)
    val currentSprint: StateFlow<Sprint?> = _currentSprint

    private val _sprintTasks = MutableStateFlow<Any?>(null)
    val sprintTasks: StateFlow<Any?> = _sprintTasks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getAllSprints(workspaceId: String? = null, status: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getAllSprints(workspaceId, status)
                if (response.isSuccessful && response.body()?.success == true) {
                    _sprints.value = response.body()?.data ?: emptyList()
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

    fun getSprintById(sprintId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getSprintById(sprintId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _currentSprint.value = response.body()?.data
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

    fun createSprint(sprint: Sprint) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.createSprint(sprint)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh sprints list after creation
                    getAllSprints(sprint.workspaceId)
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

    fun updateSprint(sprintId: String, sprint: Sprint) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateSprint(sprintId, sprint)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current sprint if it's the one being edited
                    if (_currentSprint.value?.id == sprintId) {
                        _currentSprint.value = response.body()?.data
                    }
                    // Refresh sprints list after update
                    getAllSprints(sprint.workspaceId)
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

    fun deleteSprint(sprintId: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteSprint(sprintId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Clear current sprint if it's the one being deleted
                    if (_currentSprint.value?.id == sprintId) {
                        _currentSprint.value = null
                    }
                    // Refresh sprints list after deletion
                    getAllSprints(workspaceId)
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

    fun updateSprintStatus(sprintId: String, status: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateSprintStatus(sprintId, status)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current sprint if it's the one being modified
                    if (_currentSprint.value?.id == sprintId) {
                        _currentSprint.value = response.body()?.data
                    }
                    // Refresh sprints list after update
                    getAllSprints(workspaceId)
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

    fun getSprintTasks(sprintId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getSprintTasks(sprintId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _sprintTasks.value = response.body()?.data
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