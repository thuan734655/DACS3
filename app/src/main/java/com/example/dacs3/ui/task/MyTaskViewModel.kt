package com.example.dacs3.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Task
import com.example.dacs3.data.repository.TaskRepository
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyTaskUiState(
    val isLoading: Boolean = false,
    val myTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val error: String? = null,
    val selectedStatusFilter: String? = null,
    val selectedPriorityFilter: String? = null
)

@HiltViewModel
class MyTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTaskUiState())
    val uiState: StateFlow<MyTaskUiState> = _uiState.asStateFlow()

    /**
     * Load all tasks assigned to the current user across all workspaces
     */
    fun loadMyTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = userManager.getCurrentUserId()
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "User not logged in"
                        )
                    }
                    return@launch
                }

                // Fetch all tasks assigned to the current user
                val response = taskRepository.getAllTasksFromApi(assignedTo = userId)

                if (response.success) {
                    val myTasks = response.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            myTasks = myTasks,
                            filteredTasks = myTasks
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load tasks"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Apply filters to the task list
     */
    fun applyFilters(status: String? = null, priority: String? = null) {
        viewModelScope.launch {
            val filteredList = _uiState.value.myTasks.filter { task ->
                (status == null || task.status == status) &&
                        (priority == null || task.priority == priority)
            }

            _uiState.update {
                it.copy(
                    filteredTasks = filteredList,
                    selectedStatusFilter = status,
                    selectedPriorityFilter = priority
                )
            }
        }
    }

    /**
     * Reset all filters
     */
    fun resetFilters() {
        _uiState.update {
            it.copy(
                filteredTasks = it.myTasks,
                selectedStatusFilter = null,
                selectedPriorityFilter = null
            )
        }
    }

    /**
     * Initialize the view model by loading tasks
     */
    init {
        loadMyTasks()
    }
}