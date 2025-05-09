package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.models.Message
import com.example.dacs3.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    fun sendMessage(content: String, sender: User) {
        viewModelScope.launch {
            try {
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    content = content,
                    sender = sender,
                    timestamp = System.currentTimeMillis()
                )
                _chatState.value = _chatState.value.copy(
                    messages = _chatState.value.messages + newMessage
                )
            } catch (e: Exception) {
                _chatState.value = _chatState.value.copy(
                    error = e.message ?: "Failed to send message"
                )
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            _chatState.value = _chatState.value.copy(isLoading = true)
            try {
                // TODO: Implement message loading from repository
                _chatState.value = _chatState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _chatState.value = _chatState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load messages"
                )
            }
        }
    }
} 