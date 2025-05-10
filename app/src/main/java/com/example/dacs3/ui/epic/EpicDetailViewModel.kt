package com.example.dacs3.ui.epic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.EpicDao
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.Status
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpicDetailViewModel @Inject constructor(
    private val epicDao: EpicDao,
    private val taskDao: TaskDao
) : ViewModel() {
    
    private val _epicId = MutableStateFlow<String?>(null)
    
    private val _epic = MutableStateFlow<EpicEntity?>(null)
    val epic: StateFlow<EpicEntity?> = _epic.asStateFlow()
    
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()
    
    // Edit fields
    private val _editName = MutableStateFlow("")
    val editName: StateFlow<String> = _editName.asStateFlow()
    
    private val _editDescription = MutableStateFlow("")
    val editDescription: StateFlow<String> = _editDescription.asStateFlow()
    
    private val _editPriority = MutableStateFlow(3)
    val editPriority: StateFlow<Int> = _editPriority.asStateFlow()
    
    fun setEpicId(id: String) {
        _epicId.value = id
        loadEpic(id)
        loadTasks(id)
    }
    
    private fun loadEpic(epicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val epic = epicDao.getEpicById(epicId)
                _epic.value = epic
                
                // Initialize edit fields
                epic?.let {
                    _editName.value = it.name
                    _editDescription.value = it.description
                    _editPriority.value = it.priority
                }
                
                _error.value = null
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("EpicDetailViewModel", "Epic loading job was cancelled")
                } else {
                    Log.e("EpicDetailViewModel", "Error loading epic: ${e.message}")
                    _error.value = "Failed to load epic: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadTasks(epicId: String) {
        viewModelScope.launch {
            try {
                taskDao.getTasksByEpic(epicId)
                    .catch { e ->
                        if (e is CancellationException) {
                            // Job cancellation is expected during view lifecycle changes, no need to show error
                            Log.d("EpicDetailViewModel", "Task loading job was cancelled")
                        } else {
                            Log.e("EpicDetailViewModel", "Error loading tasks: ${e.message}")
                            _error.value = "Failed to load tasks: ${e.message}"
                        }
                    }
                    .collect { taskList ->
                        _tasks.value = taskList
                    }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // Job cancellation is expected during view lifecycle changes, no need to show error
                    Log.d("EpicDetailViewModel", "Task collection job was cancelled")
                } else {
                    Log.e("EpicDetailViewModel", "Error collecting tasks: ${e.message}")
                    _error.value = "Failed to load tasks: ${e.message}"
                }
            }
        }
    }
    
    fun enterEditMode() {
        _isEditMode.value = true
    }
    
    fun cancelEdit() {
        _isEditMode.value = false
        
        // Reset edit fields to current values
        _epic.value?.let {
            _editName.value = it.name
            _editDescription.value = it.description
            _editPriority.value = it.priority
        }
    }
    
    fun updateName(name: String) {
        _editName.value = name
    }
    
    fun updateDescription(description: String) {
        _editDescription.value = description
    }
    
    fun updatePriority(priority: Int) {
        _editPriority.value = priority
    }
    
    fun updateEpicStatus(status: Status) {
        val currentEpic = _epic.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedEpic = currentEpic.copy(status = status)
                epicDao.updateEpic(updatedEpic)
                _epic.value = updatedEpic
                _error.value = null
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Log.d("EpicDetailViewModel", "Status update job was cancelled")
                } else {
                    Log.e("EpicDetailViewModel", "Error updating epic status: ${e.message}")
                    _error.value = "Failed to update status: ${e.message}"
                }
            }
        }
    }
    
    fun saveChanges() {
        val currentEpic = _epic.value ?: return
        val epicId = _epicId.value ?: return
        
        // Validate fields
        if (_editName.value.isBlank()) {
            _error.value = "Epic name cannot be empty"
            return
        }
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val updatedEpic = currentEpic.copy(
                    name = _editName.value,
                    description = _editDescription.value,
                    priority = _editPriority.value
                )
                
                epicDao.updateEpic(updatedEpic)
                _epic.value = updatedEpic
                _isEditMode.value = false
                _error.value = null
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Log.d("EpicDetailViewModel", "Save changes job was cancelled")
                } else {
                    Log.e("EpicDetailViewModel", "Error updating epic: ${e.message}")
                    _error.value = "Failed to update epic: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
} 