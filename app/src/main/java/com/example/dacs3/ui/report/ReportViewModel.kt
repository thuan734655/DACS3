package com.example.dacs3.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Task
import com.example.dacs3.data.repository.TaskRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DailyReportData(
    val date: Date = Date(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val newTasks: Int = 0,
    val totalHoursSpent: Double = 0.0,
    val tasksByWorkspace: Map<String, Int> = emptyMap(),
    val tasksByPriority: Map<String, Int> = emptyMap()
)

data class ReportUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dailyReport: DailyReportData = DailyReportData(),
    val selectedDate: Date = Date(),
    val allTasks: List<Task> = emptyList(),
    val workspaceNames: Map<String, String> = emptyMap()
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadDailyReport(Date())
    }

    fun loadDailyReport(date: Date) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, selectedDate = date) }
            
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
                
                // Load tasks for the current user
                val response = taskRepository.getAllTasksFromApi(assignedTo = userId)
                
                if (response.success && response.data != null) {
                    val allTasks = response.data
                    
                    // Load workspace names for reference
                    val workspaceNames = mutableMapOf<String, String>()
                    allTasks.forEach { task ->
                        if (!workspaceNames.containsKey(task.workspace_id)) {
                            try {
                                val workspaceResponse = workspaceRepository.getWorkspaceByIdFromApi(task.workspace_id)
                                if (workspaceResponse.success && workspaceResponse.data != null) {
                                    workspaceNames[task.workspace_id] = workspaceResponse.data.workspace.name
                                }
                            } catch (e: Exception) {
                                // Ignore workspace loading errors
                                workspaceNames[task.workspace_id] = "Unknown Workspace"
                            }
                        }
                    }
                    
                    // Filter tasks for the selected date
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val selectedDateStr = sdf.format(date)
                    
                    val tasksForDate = allTasks.filter { task ->
                        // Include tasks that were created, updated, or completed on the selected date
                        val createdDate = sdf.format(task.created_at)
                        val updatedDate = sdf.format(task.updated_at)
                        val completedDate = task.completed_date?.let { sdf.format(it) }
                        
                        createdDate == selectedDateStr || 
                        updatedDate == selectedDateStr || 
                        completedDate == selectedDateStr
                    }
                    
                    // Calculate report metrics
                    val completedTasks = tasksForDate.count { it.status.equals("done", ignoreCase = true) }
                    val inProgressTasks = tasksForDate.count { 
                        it.status.equals("in progress", ignoreCase = true) || 
                        it.status.equals("review", ignoreCase = true) 
                    }
                    val newTasks = tasksForDate.count { 
                        val createdDate = sdf.format(it.created_at)
                        createdDate == selectedDateStr 
                    }
                    
                    val totalHoursSpent = tasksForDate.sumOf { it.spent_hours.toDouble() }
                    
                    // Group tasks by workspace
                    val tasksByWorkspace = tasksForDate
                        .groupBy { it.workspace_id }
                        .mapValues { it.value.size }
                    
                    // Group tasks by priority
                    val tasksByPriority = tasksForDate
                        .groupBy { it.priority }
                        .mapValues { it.value.size }
                    
                    val reportData = DailyReportData(
                        date = date,
                        totalTasks = tasksForDate.size,
                        completedTasks = completedTasks,
                        inProgressTasks = inProgressTasks,
                        newTasks = newTasks,
                        totalHoursSpent = totalHoursSpent,
                        tasksByWorkspace = tasksByWorkspace,
                        tasksByPriority = tasksByPriority
                    )
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            dailyReport = reportData,
                            allTasks = tasksForDate,
                            workspaceNames = workspaceNames
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load tasks for report"
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
    
    fun changeDate(date: Date) {
        loadDailyReport(date)
    }
    
    fun prevDay() {
        val calendar = Calendar.getInstance()
        calendar.time = _uiState.value.selectedDate
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        loadDailyReport(calendar.time)
    }
    
    fun nextDay() {
        val calendar = Calendar.getInstance()
        calendar.time = _uiState.value.selectedDate
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        
        // Don't allow future dates
        val today = Calendar.getInstance()
        if (calendar.timeInMillis <= today.timeInMillis) {
            loadDailyReport(calendar.time)
        }
    }
    
    fun getCurrentDate(): Date {
        return _uiState.value.selectedDate
    }
}
