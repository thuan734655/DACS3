package com.example.dacs3.ui.channel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.*
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChannelDetailViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val messageDao: MessageDao,
    private val userDao: UserDao,
    private val userChannelMembershipDao: UserChannelMembershipDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _channelId = MutableStateFlow<String?>(null)
    
    private val _sendingMessage = MutableStateFlow(false)
    val sendingMessage: StateFlow<Boolean> = _sendingMessage.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    val currentUserId: String? 
        get() = sessionManager.getUserId()
    
    val channel = _channelId.filterNotNull().flatMapLatest { id ->
        flow {
            val channel = channelDao.getChannelById(id)
            emit(channel)
        }
    }
    
    val messages = _channelId.filterNotNull().flatMapLatest { id ->
        messageDao.getChannelMessages(id)
    }
    
    val members = _channelId.filterNotNull().flatMapLatest { id ->
        userChannelMembershipDao.getChannelMembers(id)
            .combine(userDao.getAllUsers()) { memberships, users ->
                val memberIds = memberships.map { it.userId }
                users.filter { it.userId in memberIds }
            }
    }
    
    // Check if user is a member of the channel
    val isUserMemberOfChannel = combine(
        _channelId.filterNotNull(),
        flow { emit(sessionManager.getUserId()) }
    ) { channelId, userId ->
        if (userId == null) return@combine false
        userChannelMembershipDao.isUserMemberOfChannel(userId, channelId)
    }
    
    fun setChannelId(id: String) {
        _channelId.value = id
    }
    
    fun sendMessage(content: String) {
        val channelId = _channelId.value ?: return
        val userId = sessionManager.getUserId() ?: return
        
        viewModelScope.launch {
            _sendingMessage.value = true
            _error.value = null
            
            try {
                // Verify user is a member of the channel
                val isMember = userChannelMembershipDao.isUserMemberOfChannel(userId, channelId)
                if (!isMember) {
                    _error.value = "You are not a member of this channel"
                    _sendingMessage.value = false
                    return@launch
                }
                
                Log.d("ChannelDetail", "Sending message to channel $channelId from user $userId")
                
                val message = MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = content,
                    senderId = userId,
                    receiverId = null,
                    channelId = channelId,
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                
                messageDao.insertMessage(message)
                
                // Mark channel as read for the sender
                val channel = channelDao.getChannelById(channelId)
                if (channel != null && channel.unreadCount > 0) {
                    channelDao.updateChannel(channel.copy(unreadCount = 0))
                }
                
                _sendingMessage.value = false
            } catch (e: Exception) {
                Log.e("ChannelDetail", "Error sending message", e)
                _error.value = "Failed to send message: ${e.message}"
                _sendingMessage.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 