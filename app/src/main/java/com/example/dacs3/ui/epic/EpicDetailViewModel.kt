package com.example.dacs3.ui.epic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.EpicDao
import com.example.dacs3.data.local.EpicEntity
import com.example.dacs3.data.local.TaskDao
import com.example.dacs3.data.local.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
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
                _error.value = null
            } catch (e: Exception) {
                Log.e("EpicDetailViewModel", "Error loading epic: ${e.message}")
                _error.value = "Failed to load epic: ${e.message}"
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
                        Log.e("EpicDetailViewModel", "Error loading tasks: ${e.message}")
                        _error.value = "Failed to load tasks: ${e.message}"
                    }
                    .collect { taskList ->
                        _tasks.value = taskList
                    }
            } catch (e: Exception) {
                Log.e("EpicDetailViewModel", "Error collecting tasks: ${e.message}")
                _error.value = "Failed to load tasks: ${e.message}"
            }
        }
    }
} 