package com.example.dacs3.ui.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.DirectMessage
import com.example.dacs3.data.model.DirectMessageRequest
import com.example.dacs3.data.model.User
import com.example.dacs3.data.repository.ChatRepository
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.repository.impl.ChatRepositoryImpl
import com.example.dacs3.data.websocket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DirectChatViewModel @Inject constructor(
    private val chatRepository: ChatRepositoryImpl,
    private val userRepository: UserRepository,
    private val webSocketManager: WebSocketManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val receiverId: String = checkNotNull(savedStateHandle["userId"])
    private val receiverName: String = checkNotNull(savedStateHandle["userName"])
    
    private var currentUserId: String = ""
    
    private val _state = MutableStateFlow(DirectChatUiState())
    val state: StateFlow<DirectChatUiState> = _state.asStateFlow()
    
    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()
    
    init {
        loadCurrentUser()
        observeSocketEvents()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                // Temporary hardcoded user ID for testing - replace with proper authentication
                currentUserId = "user123" 
                loadMessages()
                
                // In a real app, you'd get the current user like this:
                // val user = userRepository.getUserProfile()
                // if (user != null) {
                //     currentUserId = user._id
                //     loadMessages()
                // }
            } catch (e: Exception) {
                Log.e("DirectChatViewModel", "Error loading current user", e)
                _state.update { state ->
                    state.copy(
                        error = "Unable to load user information"
                    )
                }
            }
        }
    }
    
    private fun observeSocketEvents() {
        // Commented out for now since WebSocketManager implementation might be different
        // In a real app, you'd observe the WebSocket stream for new messages
        /*
        viewModelScope.launch {
            webSocketManager.getMessageFlow()
                .filter { message -> 
                    (message.senderId == receiverId && message.receiverId == currentUserId) || 
                    (message.senderId == currentUserId && message.receiverId == receiverId)
                }
                .collect { message ->
                    // Add new message to the list
                    val updatedMessages = _state.value.messages.toMutableList()
                    updatedMessages.add(0, message) // Add at the beginning (newest first)
                    
                    _state.update { currentState ->
                        currentState.copy(
                            messages = updatedMessages
                        )
                    }
                }
        }
        */
    }
    
    fun loadMessages(page: Int = 1) {
        viewModelScope.launch {
            if (currentUserId.isEmpty()) {
                return@launch
            }
            
            _state.update { it.copy(
                isLoading = true,
                error = ""
            )}
            
            try {
                val result = chatRepository.getDirectMessages(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    page = page
                )
                
                result.fold(
                    onSuccess = { messages ->
                        _state.update { it.copy(
                            isLoading = false,
                            messages = if (page == 1) messages else it.messages + messages,
                            receiver = User(_id = receiverId, name = receiverName, avatar = null, created_at = Date())
                        )}
                    },
                    onFailure = { error ->
                        Log.e("DirectChatViewModel", "Failed to load messages", error)
                        _state.update { it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load messages"
                        )}
                    }
                )
            } catch (e: Exception) {
                Log.e("DirectChatViewModel", "Error loading messages", e)
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )}
            }
        }
    }
    
    fun onMessageInputChange(input: String) {
        _messageInput.value = input
    }
    
    fun sendMessage() {
        val messageContent = _messageInput.value.trim()
        if (messageContent.isEmpty() || currentUserId.isEmpty()) {
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(
                isSending = true
            )}
            
            try {
                val result = chatRepository.sendDirectMessage(
                    receiverId = receiverId,
                    content = messageContent
                )
                
                result.fold(
                    onSuccess = { message ->
                        // Clear input field after successful send
                        _messageInput.value = ""
                        _state.update { it.copy(
                            isSending = false
                        )}
                        
                        // No need to add the message to the list as it will come through the WebSocket
                    },
                    onFailure = { error ->
                        Log.e("DirectChatViewModel", "Failed to send message", error)
                        _state.update { it.copy(
                            isSending = false,
                            error = "Failed to send message"
                        )}
                    }
                )
            } catch (e: Exception) {
                Log.e("DirectChatViewModel", "Error sending message", e)
                _state.update { it.copy(
                    isSending = false,
                    error = "An unexpected error occurred"
                )}
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = "") }
    }
}

data class DirectChatUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val messages: List<DirectMessage> = emptyList(),
    val receiver: User? = null,
    val error: String = ""
)
