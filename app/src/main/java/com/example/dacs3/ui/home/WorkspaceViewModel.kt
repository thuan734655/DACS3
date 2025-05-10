package com.example.dacs3.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.ChannelEntity
import com.example.dacs3.data.local.TaskEntity
import com.example.dacs3.data.local.UserEntity
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: WorkspaceRepository
) : ViewModel() {
    // Current user ID (would be set from auth in a real app)
    private val currentUserId = "user1"
    
    // User data
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()
    
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
    
    init {
        viewModelScope.launch {
            // Seed initial data
            repository.seedInitialData()
            
            // Load current user
            loadCurrentUser()
            
            // Load channels
            loadChannels()
            
            // Load direct message contacts
            loadDirectMessageContacts()
            
            // Load tasks
            loadUserTasks()
            
            // Count unread messages
            countUnreadMessages()
        }
    }
    
    private suspend fun loadCurrentUser() {
        repository.getUserById(currentUserId)?.let { user ->
            _currentUser.value = user
        }
    }
    
    private suspend fun loadChannels() {
        repository.getAllChannels().collect { allChannels ->
            _channels.value = allChannels
        }
    }
    
    private fun loadUserTasks() {
        viewModelScope.launch {
            repository.getUserTasks(currentUserId).collect { tasks ->
                _userTasks.value = tasks
            }
        }
    }
    
    private suspend fun loadDirectMessageContacts() {
        repository.getAllUsers().collect { allUsers ->
            _directMessageContacts.value = allUsers.filter { it.userId != currentUserId }
        }
    }
    
    private suspend fun countUnreadMessages() {
        repository.getUnreadMessages(currentUserId).collect { messages ->
            _unreadMessageCount.value = messages.size
        }
    }
    
    fun refreshData() {
        viewModelScope.launch {
            loadCurrentUser()
            loadChannels()
            loadDirectMessageContacts()
            loadUserTasks()
            countUnreadMessages()
        }
    }
} 