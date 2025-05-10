package com.example.dacs3.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.*
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: WorkspaceRepository,
    @ApplicationContext private val context: Context
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
    
    // Workspace channels
    private val _workspaceChannels = MutableStateFlow<List<ChannelEntity>>(emptyList())
    val workspaceChannels: StateFlow<List<ChannelEntity>> = _workspaceChannels.asStateFlow()
    
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
    
    // Currently selected workspace
    private val _selectedWorkspace = MutableStateFlow<WorkspaceEntity?>(null)
    val selectedWorkspace: StateFlow<WorkspaceEntity?> = _selectedWorkspace.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Initialize empty repository without initial data
            // repository.seedInitialData() - Remove this line to prevent any data seeding
            
            // Load saved workspace preference
            loadSavedWorkspacePreference()
        }
    }
    
    private fun loadSavedWorkspacePreference() {
        val sharedPrefs = context.getSharedPreferences("workspace_prefs", Context.MODE_PRIVATE)
        val savedWorkspaceId = sharedPrefs.getString("selected_workspace_id", null)
        
        if (savedWorkspaceId != null) {
            _selectedWorkspaceId.value = savedWorkspaceId
            viewModelScope.launch {
                repository.getWorkspaceById(savedWorkspaceId)?.let { workspace ->
                    _selectedWorkspace.value = workspace
                    loadEpicsForWorkspace(savedWorkspaceId)
                }
            }
        }
    }
    
    // Initial data loading when a user logs in
    fun setCurrentUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value = userId
            
            // Load current user
            loadCurrentUser()
            
                // Load workspaces
                loadUserWorkspaces()
                
                // Check for selected workspace
                if (_selectedWorkspaceId.value != null) {
                    // If we have a selected workspace already, load its data
                    val workspaceId = _selectedWorkspaceId.value!!
                    loadWorkspaceChannels(workspaceId)
                    loadEpicsForWorkspace(workspaceId)
                } else {
                    // Try to load first workspace
                    loadDefaultWorkspace(userId)
                }
                
                // Load direct message contacts (excluding current user)
            loadDirectMessageContacts()
                
                // Load user tasks
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
    
    // Helper method to load the first workspace if none is selected
    private fun loadDefaultWorkspace(userId: String) {
        viewModelScope.launch {
            try {
                val workspaces = repository.getUserWorkspaces(userId).first()
                if (workspaces.isNotEmpty()) {
                    selectWorkspace(workspaces[0].workspaceId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Could not load default workspace: ${e.message}"
            }
        }
    }
    
    fun selectWorkspace(workspaceId: String) {
        _selectedWorkspaceId.value = workspaceId
        viewModelScope.launch {
            try {
                repository.getWorkspaceById(workspaceId)?.let { workspace ->
                    _selectedWorkspace.value = workspace
                    
                    // Save the selected workspace preference
                    val sharedPrefs = context.getSharedPreferences("workspace_prefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putString("selected_workspace_id", workspaceId).apply()
                    
                    // Immediately load channels for this workspace
                    loadWorkspaceChannels(workspaceId)
                    
                    // Load epics for this workspace
                    loadEpicsForWorkspace(workspaceId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error selecting workspace: ${e.message}"
            }
        }
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
            
            // Also load workspace channels if we have a selected workspace
            _selectedWorkspaceId.value?.let { workspaceId ->
                loadWorkspaceChannels(workspaceId)
            }
        }
    }
    
    private fun loadWorkspaceChannels(workspaceId: String) {
        viewModelScope.launch {
            try {
                // Clear existing channels first
                _workspaceChannels.value = emptyList()
                
                // Load only channels for this workspace
                repository.getChannelsByWorkspace(workspaceId).collect { channels ->
                    _workspaceChannels.value = channels
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading workspace channels: ${e.message}"
            }
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
        // Validate input first
        if (name.isBlank()) {
            _errorMessage.value = "Workspace name cannot be empty"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUserId.value?.let { userId ->
                    // Ensure that the user exists first before creating a workspace
                    // This prevents foreign key constraint issues
                    val existingUser = repository.getUserById(userId)
                    
                    // Create or update the user first
                    val user = if (existingUser == null) {
                        // Create a default user if it doesn't exist
                        val defaultUser = UserEntity(
                            userId = userId,
                            username = "User $userId",
                            email = "user$userId@example.com",
                            password = "password123",
                            avatarUrl = null,
                            isOnline = true
                        )
                        // Insert and wait for completion
                        repository.insertUser(defaultUser)
                        
                        // Wait a moment to ensure the insert completes
                        kotlinx.coroutines.delay(500)
                        
                        // Verify the user was inserted
                        val createdUser = repository.getUserById(userId)
                        if (createdUser == null) {
                            throw IllegalStateException("Failed to create user with ID: $userId")
                        }
                        _currentUser.value = defaultUser
                        defaultUser
                    } else {
                        existingUser
                    }
                    
                    // Generate a unique workspace ID
                    val workspaceId = UUID.randomUUID().toString()
                    
                    // Now create the workspace with the verified user
                    val workspace = WorkspaceEntity(
                        workspaceId = workspaceId,
                        name = name,
                        description = description,
                        createdBy = user.userId,
                        leaderId = user.userId
                    )
                    
                    // Log for debugging
                    android.util.Log.d("WorkspaceViewModel", "Creating workspace: $name with ID: $workspaceId")
                    
                    try {
                        // Create workspace and membership in a single transaction
                        repository.createWorkspaceWithMembership(workspace, user.userId)
                        
                        // Refresh the workspace list
                        loadUserWorkspaces()
                        
                        // Set it as the selected workspace
                        selectWorkspace(workspace.workspaceId)
                        
                        // Clear any error messages
                        _errorMessage.value = null
                    } catch (e: Exception) {
                        throw IllegalStateException("Error creating workspace: ${e.message}")
                    }
                } ?: run {
                    throw IllegalStateException("No user is currently logged in")
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error creating workspace: ${e.message}"
                _isLoading.value = false
                
                // Log the error
                android.util.Log.e("WorkspaceViewModel", "Error creating workspace", e)
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
                    
                    // Use safe method to ensure proper relationships
                    repository.safeInsertChannel(channel, userId)
                    
                    // Refresh the channel list for the current workspace
                    loadWorkspaceChannels(workspaceId)
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
                    
                    // Use safe method to ensure proper relationships
                    repository.safeInsertMessage(message)
                    
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