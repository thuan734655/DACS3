package com.example.dacs3.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.*
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
    private val userChannelMembershipDao: UserChannelMembershipDao
) : ViewModel() {
    
    private val _channelId = MutableStateFlow<String?>(null)
    
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
    
    fun setChannelId(id: String) {
        _channelId.value = id
        loadChannelData(id)
    }
    
    private fun loadChannelData(channelId: String) {
        viewModelScope.launch {
            // No longer load sample data - just load actual data from the database
            // The user doesn't want mock data
        }
    }
    
    fun sendMessage(content: String) {
        val channelId = _channelId.value ?: return
        
        viewModelScope.launch {
            val message = MessageEntity(
                messageId = UUID.randomUUID().toString(),
                content = content,
                senderId = "user1", // Hardcoded for now, would be the current user
                receiverId = null,
                channelId = channelId,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            
            messageDao.insertMessage(message)
        }
    }
} 