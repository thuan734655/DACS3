//package com.example.dacs3.ui.task
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.dacs3.data.model.Epic
//import com.example.dacs3.data.model.Task
//import com.example.dacs3.data.model.Sprint
//import com.example.dacs3.data.model.User
//import com.example.dacs3.data.repository.EpicRepository
//import com.example.dacs3.data.repository.TaskRepository
//import com.example.dacs3.data.repository.WorkspaceRepository
//import com.example.dacs3.data.repository.SprintRepository
//import com.example.dacs3.data.repository.UserRepository
//import com.example.dacs3.data.session.SessionManager
//import com.example.dacs3.data.user.UserManager
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import java.util.Date
//import javax.inject.Inject
//
//data class TaskUiState(
//    val isLoading: Boolean = false,
//    val tasks: List<Task> = emptyList(),
//    val filteredTasks: List<Task> = emptyList(),
//    val availableEpics: List<Epic> = emptyList(),
//    val error: String? = null,
//    val isCreationSuccessful: Boolean = false,
//    val isUpdateSuccessful: Boolean = false,
//    val isDeleteSuccessful: Boolean = false,
//    val selectedTask: Task? = null,
//    val currentWorkspaceId: String? = null,
//    val currentEpicId: String? = null,
//    val isLoadingEpics: Boolean = false,
//    val workspaceUsers: List<User> = emptyList(),
//    val isLoadingUsers: Boolean = false,
//    val workspaceSprints: List<Sprint> = emptyList(),
//    val isLoadingSprints: Boolean = false
//)
//
//@HiltViewModel
//class TaskViewModel @Inject constructor(
//    private val taskRepository: TaskRepository,
//    private val epicRepository: EpicRepository,
//    private val workspaceRepository: WorkspaceRepository,
//    private val sprintRepository: SprintRepository,
//    private val userRepository: UserRepository,
//    private val sessionManager: SessionManager,
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(TaskUiState())
//    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
//
//    fun loadTasksByEpic(epicId: String) {
//        _uiState.update { it.copy(currentEpicId = epicId, currentWorkspaceId = null) }
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//
//            try {
//                // First try to get from API
//                val response = taskRepository.getAllTasksFromApi(epicId = epicId)
//
//                if (response.success) {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = response.data ?: emptyList(),
//                            filteredTasks = response.data ?: emptyList()
//                        )
//                    }
//                } else {
//                    // If API fails, fall back to locally cached data
//                    taskRepository.getTasksByEpicId(epicId).collect { taskEntities ->
//                        val tasks = taskEntities.map { it.toTask() }
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                tasks = tasks,
//                                filteredTasks = tasks
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                // On exception, try to load from local database
//                taskRepository.getTasksByEpicId(epicId).collect { taskEntities ->
//                    val tasks = taskEntities.map { it.toTask() }
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = tasks,
//                            filteredTasks = tasks,
//                            error = "Could not connect to server. Showing cached data."
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    fun loadTasksByWorkspace(workspaceId: String) {
//        _uiState.update { it.copy(currentWorkspaceId = workspaceId, currentEpicId = null) }
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//
//            try {
//                // First try to get from API
//                val response = taskRepository.getAllTasksFromApi(workspaceId = workspaceId)
//
//                if (response.success) {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = response.data ?: emptyList(),
//                            filteredTasks = response.data ?: emptyList()
//                        )
//                    }
//                } else {
//                    // If API fails, fall back to locally cached data
//                    taskRepository.getTasksByWorkspaceId(workspaceId).collect { taskEntities ->
//                        val tasks = taskEntities.map { it.toTask() }
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                tasks = tasks,
//                                filteredTasks = tasks
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                // On exception, try to load from local database
//                taskRepository.getTasksByWorkspaceId(workspaceId).collect { taskEntities ->
//                    val tasks = taskEntities.map { it.toTask() }
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = tasks,
//                            filteredTasks = tasks,
//                            error = "Could not connect to server. Showing cached data."
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    fun createTask(
//        title: String,
//        description: String?,
//        workspaceId: String,
//        epicId: String?,
//        assignedTo: String?,
//        status: String,
//        priority: String,
//        estimatedHours: Number,
//        sprintId: String?,
//        startDate: Date?,
//        dueDate: Date?
//    ) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null, isCreationSuccessful = false) }
//
//            try {
//                val userId = sessionManager.getUserId() ?: throw IllegalStateException("User not logged in")
//
//                val response = taskRepository.createTask(
//                    title = title,
//                    description = description,
//                    workspaceId = workspaceId,
//                    epicId = epicId,
//                    assignedTo = assignedTo,
//                    status = status,
//                    priority = priority,
//                    estimatedHours = estimatedHours,
//                    spentHours = 0,
//                    startDate = startDate,
//                    dueDate = dueDate,
//                    sprintId = sprintId
//                )
//
//                if (response.success && response.data != null) {
//                    // Update UI state with the new task
//                    val updatedTasks = _uiState.value.tasks.toMutableList()
//                    updatedTasks.add(response.data)
//
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = updatedTasks,
//                            filteredTasks = updatedTasks.filter { task ->
//                                (it.currentEpicId != null && task.epic_id == it.currentEpicId) ||
//                                (it.currentWorkspaceId != null && task.workspace_id == it.currentWorkspaceId)
//                            },
//                            isCreationSuccessful = true,
//                            selectedTask = response.data
//                        )
//                    }
//
//                    // Refresh tasks list
//                    if (_uiState.value.currentEpicId != null) {
//                        loadTasksByEpic(_uiState.value.currentEpicId!!)
//                    } else if (_uiState.value.currentWorkspaceId != null) {
//                        loadTasksByWorkspace(_uiState.value.currentWorkspaceId!!)
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            error = "Failed to create task"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = "Error: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun selectTask(task: Task) {
//        _uiState.update { it.copy(selectedTask = task) }
//    }
//
//    fun filterTasksByStatus(status: String?) {
//        val allTasks = _uiState.value.tasks
//        val filtered = if (status == null || status == "All") {
//            allTasks
//        } else {
//            allTasks.filter { it.status == status }
//        }
//
//        _uiState.update { it.copy(filteredTasks = filtered) }
//    }
//
//    fun filterTasksByPriority(priority: String?) {
//        val allTasks = _uiState.value.tasks
//        val filtered = if (priority == null || priority == "All") {
//            allTasks
//        } else {
//            allTasks.filter { it.priority == priority }
//        }
//
//        _uiState.update { it.copy(filteredTasks = filtered) }
//    }
//
//    fun clearSelection() {
//        _uiState.update { it.copy(selectedTask = null) }
//    }
//
//    fun resetCreationState() {
//        _uiState.update { it.copy(isCreationSuccessful = false, error = null) }
//    }
//
//    fun resetUpdateState() {
//        _uiState.update { it.copy(isUpdateSuccessful = false, error = null) }
//    }
//
//    fun resetDeleteState() {
//        _uiState.update { it.copy(isDeleteSuccessful = false, error = null) }
//    }
//
//    fun loadEpicsForWorkspace(workspaceId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoadingEpics = true) }
//
//            try {
//                val response = epicRepository.getAllEpicsFromApi(workspaceId = workspaceId)
//
//                if (response.success) {
//                    _uiState.update {
//                        it.copy(
//                            availableEpics = response.data ?: emptyList(),
//                            isLoadingEpics = false
//                        )
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isLoadingEpics = false,
//                            error = "Failed to load epics"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoadingEpics = false,
//                        error = "Error loading epics: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun updateTask(
//        id: String,
//        title: String?,
//        description: String?,
//        epicId: String?,
//        assignedTo: String?,
//        status: String?,
//        priority: String?,
//        estimatedHours: Number?,
//        spentHours: Number?,
//        startDate: Date?,
//        dueDate: Date?,
//        completedDate: Date?,
//        sprintId: String?
//    ) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null, isUpdateSuccessful = false) }
//
//            try {
//                val response = taskRepository.updateTask(
//                    id = id,
//                    title = title,
//                    description = description,
//                    epicId = epicId,
//                    assignedTo = assignedTo,
//                    status = status,
//                    priority = priority,
//                    estimatedHours = estimatedHours,
//                    spentHours = spentHours,
//                    startDate = startDate,
//                    dueDate = dueDate,
//                    completedDate = completedDate,
//                    sprintId = sprintId
//                )
//
//                if (response.success && response.data != null) {
//                    // Update UI state with the updated task
//                    val updatedTasks = _uiState.value.tasks.toMutableList()
//                    val index = updatedTasks.indexOfFirst { it._id == id }
//
//                    if (index != -1) {
//                        updatedTasks[index] = response.data
//                    }
//
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = updatedTasks,
//                            filteredTasks = updatedTasks.filter { task ->
//                                (it.currentEpicId != null && task.epic_id == it.currentEpicId) ||
//                                (it.currentWorkspaceId != null && task.workspace_id == it.currentWorkspaceId)
//                            },
//                            isUpdateSuccessful = true,
//                            selectedTask = response.data
//                        )
//                    }
//
//                    // Refresh tasks list
//                    if (_uiState.value.currentEpicId != null) {
//                        loadTasksByEpic(_uiState.value.currentEpicId!!)
//                    } else if (_uiState.value.currentWorkspaceId != null) {
//                        loadTasksByWorkspace(_uiState.value.currentWorkspaceId!!)
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            error = "Failed to update task"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = "Error: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun deleteTask(id: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null, isDeleteSuccessful = false) }
//
//            try {
//                val response = taskRepository.deleteTask(id)
//
//                if (response.success) {
//                    // Remove the deleted task from the list
//                    val updatedTasks = _uiState.value.tasks.filter { it._id != id }
//
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            tasks = updatedTasks,
//                            filteredTasks = updatedTasks.filter { task ->
//                                (it.currentEpicId != null && task.epic_id == it.currentEpicId) ||
//                                (it.currentWorkspaceId != null && task.workspace_id == it.currentWorkspaceId)
//                            },
//                            isDeleteSuccessful = true
//                        )
//                    }
//
//                    // If we were viewing this task, clear selection
//                    if (_uiState.value.selectedTask?._id == id) {
//                        _uiState.update { it.copy(selectedTask = null) }
//                    }
//
//                    // Refresh tasks list
//                    if (_uiState.value.currentEpicId != null) {
//                        loadTasksByEpic(_uiState.value.currentEpicId!!)
//                    } else if (_uiState.value.currentWorkspaceId != null) {
//                        loadTasksByWorkspace(_uiState.value.currentWorkspaceId!!)
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            error = "Failed to delete task"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = "Error: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun loadWorkspaceUsers(workspaceId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoadingUsers = true) }
//            try {
//                // Get workspace members from API
//                val response = workspaceRepository.getWorkspaceMembersFromApi(workspaceId)
//                if (response.success) {
//                    // Convert members to User list for dropdown (make sure all members are included)
//                    val members = response.data?.map {
//                        User(
//                            _id = it._id,
//                            name = it.name,
//                            avatar = it.avatar,
//                            created_at = it.created_at
//                        )
//                    } ?: emptyList()
//
//                    _uiState.update {
//                        it.copy(
//                            isLoadingUsers = false,
//                            workspaceUsers = members
//                        )
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isLoadingUsers = false,
//                            error = "Failed to load workspace members"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoadingUsers = false,
//                        error = "Error loading workspace members: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun loadWorkspaceSprints(workspaceId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoadingSprints = true) }
//            try {
//                // Get sprints for the workspace from API
//                val response = sprintRepository.getAllSprintsFromApi(workspaceId = workspaceId)
//                if (response.success) {
//                    _uiState.update {
//                        it.copy(
//                            isLoadingSprints = false,
//                            workspaceSprints = response.data ?: emptyList()
//                        )
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isLoadingSprints = false,
//                            error = "Failed to load sprints"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                // Try loading from local database on failure
//                try {
//                    sprintRepository.getSprintsByWorkspaceId(workspaceId).collect { sprintEntities ->
//                        val sprints = sprintEntities.map { it.toSprint() }
//                        _uiState.update {
//                            it.copy(
//                                isLoadingSprints = false,
//                                workspaceSprints = sprints
//                            )
//                        }
//                    }
//                } catch (e2: Exception) {
//                    _uiState.update {
//                        it.copy(
//                            isLoadingSprints = false,
//                            error = "Error loading sprints: ${e.message}"
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    fun getTaskById(taskId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//            try {
//                val response = taskRepository.getTaskById(taskId)
//                if (response.success && response.data != null) {
//                    _uiState.update { it.copy(isLoading = false, selectedTask = response.data) }
//                } else {
//                    _uiState.update { it.copy(isLoading = false, error = "Không thể tải thông tin công việc") }
//                }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(isLoading = false, error = "Lỗi: ${e.message}") }
//            }
//        }
//    }
//
//    fun getTasksByEpicId(epicId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//            try {
//                val response = taskRepository.getAllTasksFromApi(epicId = epicId)
//                if (response.success) {
//                    _uiState.update { it.copy(isLoading = false, tasks = response.data ?: emptyList()) }
//                } else {
//                    _uiState.update { it.copy(isLoading = false, error = "Không thể tải danh sách công việc") }
//                }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(isLoading = false, error = "Lỗi: ${e.message}") }
//            }
//        }
//    }
//
//    fun getAllTasks(workspaceId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//            try {
//                val response = taskRepository.getAllTasksFromApi(workspaceId = workspaceId)
//                if (response.success) {
//                    _uiState.update { it.copy(isLoading = false, tasks = response.data ?: emptyList()) }
//                } else {
//                    _uiState.update { it.copy(isLoading = false, error = "Không thể tải danh sách công việc") }
//                }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(isLoading = false, error = "Lỗi: ${e.message}") }
//            }
//        }
//    }
//}
//
