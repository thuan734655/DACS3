package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.RealtimeMessage
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.repository.FirebaseMessageRepository
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val firebaseMessageRepository: FirebaseMessageRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // State for current workspace
    private val _currentWorkspace = MutableStateFlow<Workspace?>(null)
    val currentWorkspace = _currentWorkspace.asStateFlow()

    // State for current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    // State for workspace messages
    private val _workspaceMessages = MutableStateFlow<List<RealtimeMessage>>(emptyList())
    val workspaceMessages = _workspaceMessages.asStateFlow()

    // State for all conversations
    val conversations = firebaseMessageRepository.getUserWorkspaceConversations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Error states
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Message input
    private val _messageInput = MutableStateFlow("")
    val messageInput = _messageInput.asStateFlow()

    init {
        loadCurrentUser()
    }

    /**
     * Load the current user information
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val userResponse = userRepository.getUserByIdFromApi(userId)
                    if (userResponse.success && userResponse.data != null) {
                        _currentUser.value = userResponse.data
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load user information: ${e.message}"
            }
        }
    }

    /**
     * Load a workspace by ID and initialize its messages
     */
    fun loadWorkspace(workspaceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load workspace details
                val workspaceResponse = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
                if (workspaceResponse.success && workspaceResponse.data != null) {
                    _currentWorkspace.value = workspaceResponse.data
                    
                    // Listen for messages in this workspace
                    listenForWorkspaceMessages(workspaceId)
                    
                    // Mark messages as read
                    firebaseMessageRepository.markWorkspaceMessagesAsRead(workspaceId)
                } else {
                    _error.value = "Workspace not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load workspace: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set up a listener for workspace messages
     */
    private fun listenForWorkspaceMessages(workspaceId: String) {
        viewModelScope.launch {
            firebaseMessageRepository.getWorkspaceMessages(workspaceId).collectLatest { messages ->
                _workspaceMessages.value = messages
            }
        }
    }

    /**
     * Send a message to the current workspace
     */
    fun sendMessage() {
        val content = _messageInput.value.trim()
        val workspace = _currentWorkspace.value
        val user = _currentUser.value
        
        if (content.isEmpty() || workspace == null || user == null) {
            return
        }
        
        viewModelScope.launch {
            try {
                val success = firebaseMessageRepository.sendMessage(
                    workspaceId = workspace._id,
                    content = content,
                    senderName = user.name
                )
                
                if (success) {
                    // Clear the input field after successful send
                    _messageInput.value = ""
                } else {
                    _error.value = "Failed to send message"
                }
            } catch (e: Exception) {
                _error.value = "Error sending message: ${e.message}"
            }
        }
    }

    /**
     * Create a conversation for a workspace if it doesn't exist
     */
    fun createWorkspaceConversationIfNeeded(workspace: Workspace) {
        viewModelScope.launch {
            try {
                // Check if the workspace already has a conversation in conversations StateFlow
                val existingConversation = conversations.value.find { it.id == workspace._id }
                
                if (existingConversation == null) {
                    // Convert workspace members to list of Users
                    val members = workspace.members?.map { it.user_id } ?: emptyList()
                    
                    firebaseMessageRepository.createWorkspaceConversation(
                        workspaceId = workspace._id,
                        workspaceName = workspace.name,
                        members = members
                    )
                }
            } catch (e: Exception) {
                _error.value = "Failed to create conversation: ${e.message}"
            }
        }
    }

    /**
     * Update the message input field
     */
    fun updateMessageInput(input: String) {
        _messageInput.value = input
    }

    /**
     * Clear any error
     */
    fun clearError() {
        _error.value = null
    }
}
