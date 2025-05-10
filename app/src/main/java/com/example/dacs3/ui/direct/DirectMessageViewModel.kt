package com.example.dacs3.ui.direct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.MessageDao
import com.example.dacs3.data.local.MessageEntity
import com.example.dacs3.data.local.UserDao
import com.example.dacs3.data.local.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DirectMessageViewModel @Inject constructor(
    private val userDao: UserDao,
    private val messageDao: MessageDao
) : ViewModel() {
    
    private val _userId = MutableStateFlow<String?>(null)
    private val _currentUserId = MutableStateFlow<String?>(null)
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // The other user in the conversation
    val otherUser = _userId.filterNotNull().flatMapLatest { id ->
        flow {
            val user = userDao.getUserById(id)
            emit(user)
        }
    }
    
    // The current user
    val currentUser = _currentUserId.filterNotNull().flatMapLatest { id ->
        flow {
            val user = userDao.getUserById(id)
            emit(user)
        }
    }
    
    // Direct messages between current user and other user
    val messages = combine(
        _currentUserId.filterNotNull(),
        _userId.filterNotNull()
    ) { currentId, otherId ->
        Pair(currentId, otherId)
    }.flatMapLatest { (currentId, otherId) ->
        messageDao.getDirectMessages(currentId, otherId)
    }
    
    fun setUsers(currentUserId: String, otherUserId: String) {
        _currentUserId.value = currentUserId
        _userId.value = otherUserId
    }
    
    fun sendMessage(content: String) {
        val currentId = _currentUserId.value ?: return
        val otherUserId = _userId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val message = MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = content,
                    senderId = currentId,
                    receiverId = otherUserId,
                    channelId = null, // Direct messages don't belong to a channel
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                
                messageDao.insertMessage(message)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error sending message: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
} 