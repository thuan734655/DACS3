package com.example.dacs3.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.*
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: WorkspaceRepository
) : ViewModel() {
    // Current user ID (would be set from auth in a real app)
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()
    
    // User data
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()
    
    // Workspaces
    private val _userWorkspaces = MutableStateFlow<List<WorkspaceEntity>>(emptyList())
    val userWorkspaces: StateFlow<List<WorkspaceEntity>> = _userWorkspaces.asStateFlow()
    
    // Epics
    private val _epics = MutableStateFlow<List<EpicEntity>>(emptyList())
    val epics: StateFlow<List<EpicEntity>> = _epics.asStateFlow()
    
    // Channels
    private val _channels = MutableStateFlow<List<ChannelEntity>>(emptyList())
    val channels: StateFlow<List<ChannelEntity>> = _channels.asStateFlow()
    
    // Direct messages contacts
    private val _directMessageContacts = MutableStateFlow<List<UserEntity>>(emptyList())
    val directMessageContacts: StateFlow<List<UserEntity>> = _directMessageContacts.asStateFlow()
    
    // Tasks
    private val _userTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val userTasks: StateFlow<List<TaskEntity>> = _userTasks.asStateFlow()
    
    // Unread message count
    private val _unreadMessageCount = MutableStateFlow(0)
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Selected workspace
    private val _selectedWorkspaceId = MutableStateFlow<String?>(null)
    val selectedWorkspaceId: StateFlow<String?> = _selectedWorkspaceId.asStateFlow()
    
    // Selected epic
    private val _selectedEpicId = MutableStateFlow<String?>(null)
    val selectedEpicId: StateFlow<String?> = _selectedEpicId.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Initialize empty repository
            repository.seedInitialData()
        }
    }
    
    fun setCurrentUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value = userId
                
                // Load current user
                loadCurrentUser()
                
                // Load workspaces
                loadUserWorkspaces()
                
                // Load channels
                loadChannels()
                
                // Load direct message contacts
                loadDirectMessageContacts()
                
                // Load tasks
                loadUserTasks()
                
                // Count unread messages
                countUnreadMessages()
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading user data: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun selectWorkspace(workspaceId: String) {
        _selectedWorkspaceId.value = workspaceId
        loadEpicsForWorkspace(workspaceId)
    }
    
    fun selectEpic(epicId: String) {
        _selectedEpicId.value = epicId
        loadTasksForEpic(epicId)
    }
    
    private fun loadEpicsForWorkspace(workspaceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getEpicsByWorkspace(workspaceId).collect { epics ->
                    _epics.value = epics
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading epics: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    private fun loadTasksForEpic(epicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTasksByEpic(epicId).collect { tasks ->
                    _userTasks.value = tasks
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading tasks: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun loadCurrentUser() {
        _currentUserId.value?.let { userId ->
            repository.getUserById(userId)?.let { user ->
                _currentUser.value = user
            }
        }
    }
    
    private suspend fun loadUserWorkspaces() {
        _currentUserId.value?.let { userId ->
            repository.getUserWorkspaces(userId).collect { workspaces ->
                _userWorkspaces.value = workspaces
            }
        }
    }
    
    private suspend fun loadChannels() {
        repository.getAllChannels().collect { allChannels ->
            _channels.value = allChannels
        }
    }
    
    private fun loadUserTasks() {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.getUserTasks(userId).collect { tasks ->
                    _userTasks.value = tasks
                }
            }
        }
    }
    
    private suspend fun loadDirectMessageContacts() {
        repository.getAllUsers().collect { allUsers ->
            _currentUserId.value?.let { userId ->
                _directMessageContacts.value = allUsers.filter { it.userId != userId }
            }
        }
    }
    
    private suspend fun countUnreadMessages() {
        _currentUserId.value?.let { userId ->
            repository.getUnreadMessages(userId).collect { messages ->
                _unreadMessageCount.value = messages.size
            }
        }
    }
    
    fun createWorkspace(name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value?.let { userId ->
                    val workspace = WorkspaceEntity(
                        workspaceId = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        createdBy = userId,
                        leaderId = userId
                    )
                    
                    // Insert the workspace
                    repository.insertWorkspace(workspace)
                    
                    // Add the creator as a member with admin role
                    repository.insertWorkspaceUserMembership(
                        WorkspaceUserMembership(
                            userId = userId,
                            workspaceId = workspace.workspaceId,
                            role = "admin"
                        )
                    )
                    
                    // Refresh the workspace list
                    loadUserWorkspaces()
                    
                    // Set it as the selected workspace
                    selectWorkspace(workspace.workspaceId)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error creating workspace: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun createEpic(workspaceId: String, name: String, description: String, priority: Int = 3) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value?.let { userId ->
                    val epic = EpicEntity(
                        epicId = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        workspaceId = workspaceId,
                        createdBy = userId,
                        priority = priority,
                        status = Status.TO_DO
                    )
                    
                    // Insert the epic
                    repository.insertEpic(epic)
                    
                    // Refresh the epics list
                    loadEpicsForWorkspace(workspaceId)
                    
                    // Set it as the selected epic
                    selectEpic(epic.epicId)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error creating epic: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun createChannel(workspaceId: String, name: String, description: String, isPrivate: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value?.let { userId ->
                    val channel = ChannelEntity(
                        channelId = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        workspaceId = workspaceId,
                        createdBy = userId,
                        isPrivate = isPrivate,
                        unreadCount = 0
                    )
                    
                    // Insert the channel
                    repository.insertChannel(channel)
                    
                    // Add the creator as a member with admin role
                    repository.insertUserChannelMembership(
                        UserChannelMembership(
                            userId = userId,
                            channelId = channel.channelId,
                            joinedAt = System.currentTimeMillis(),
                            role = "admin"
                        )
                    )
                    
                    // Refresh the channel list
                    loadChannels()
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error creating channel: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun createTask(epicId: String, name: String, description: String, priority: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value?.let { userId ->
                    val task = TaskEntity(
                        taskId = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        progress = 0,
                        createdBy = userId,
                        assignedToUserId = userId,
                        epicId = epicId,
                        status = Status.TO_DO,
                        priority = priority
                    )
                    
                    // Insert the task
                    repository.insertTask(task)
                    
                    // Refresh the task list for this epic
                    loadTasksForEpic(epicId)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error creating task: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun sendMessage(receiverId: String? = null, channelId: String? = null, content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value?.let { userId ->
                    // Create a new message
                    val message = MessageEntity(
                        messageId = UUID.randomUUID().toString(),
                        content = content,
                        senderId = userId,
                        receiverId = receiverId,
                        channelId = channelId,
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                    
                    // Insert the message
                    repository.insertMessage(message)
                    
                    // Refresh unread message count
                    countUnreadMessages()
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error sending message: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun updateTaskProgress(taskId: String, progress: Int) {
        viewModelScope.launch {
            try {
                repository.getTaskById(taskId)?.let { task ->
                    val updatedTask = task.copy(progress = progress)
                    repository.updateTask(updatedTask)
                    
                    // Reload tasks for the current epic
                    _selectedEpicId.value?.let { epicId ->
                        loadTasksForEpic(epicId)
                    } ?: loadUserTasks()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating task progress: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadCurrentUser()
                loadUserWorkspaces()
                loadChannels()
                loadDirectMessageContacts()
                
                _selectedEpicId.value?.let { epicId ->
                    loadTasksForEpic(epicId)
                } ?: _selectedWorkspaceId.value?.let { workspaceId ->
                    loadEpicsForWorkspace(workspaceId)
                    loadUserTasks()
                } ?: loadUserTasks()
                
                countUnreadMessages()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error refreshing data: ${e.message}"
                _isLoading.value = false
            }
        }
    }
} 