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
            // Load sample data if the database is empty
            val messageCount = messageDao.getMessageCountByChannelId(channelId)
            if (messageCount == 0) {
                loadSampleMessages(channelId)
            }
            
            val memberCount = userChannelMembershipDao.getChannelMemberCount(channelId)
            if (memberCount == 0) {
                loadSampleMembers(channelId)
            }
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
    
    private suspend fun loadSampleMessages(channelId: String) {
        val sampleMessages = listOf(
            MessageEntity(
                messageId = UUID.randomUUID().toString(),
                content = "Hello everyone! Welcome to the channel.",
                senderId = "user1",
                receiverId = null,
                channelId = channelId,
                timestamp = System.currentTimeMillis() - 3600000 * 24, // 1 day ago
                isRead = true
            ),
            MessageEntity(
                messageId = UUID.randomUUID().toString(),
                content = "Thanks for setting this up!",
                senderId = "user2",
                receiverId = null,
                channelId = channelId,
                timestamp = System.currentTimeMillis() - 3600000 * 12, // 12 hours ago
                isRead = true
            ),
            MessageEntity(
                messageId = UUID.randomUUID().toString(),
                content = "I have a question about the project. When is our next meeting?",
                senderId = "user3",
                receiverId = null,
                channelId = channelId,
                timestamp = System.currentTimeMillis() - 3600000 * 5, // 5 hours ago
                isRead = true
            ),
            MessageEntity(
                messageId = UUID.randomUUID().toString(),
                content = "We're meeting tomorrow at 2 PM. I'll send a calendar invite.",
                senderId = "user1",
                receiverId = null,
                channelId = channelId,
                timestamp = System.currentTimeMillis() - 3600000 * 2, // 2 hours ago
                isRead = true
            )
        )
        
        messageDao.insertMessages(sampleMessages)
    }
    
    private suspend fun loadSampleMembers(channelId: String) {
        // Ensure we have users first
        val userCount = userDao.getUserCount()
        if (userCount == 0) {
            val sampleUsers = listOf(
                UserEntity(
                    userId = "user1",
                    username = "John Doe",
                    isOnline = true,
                    avatarUrl = null
                ),
                UserEntity(
                    userId = "user2",
                    username = "Ali Sarraf",
                    isOnline = true,
                    avatarUrl = null
                ),
                UserEntity(
                    userId = "user3",
                    username = "Jane Smith",
                    isOnline = false,
                    avatarUrl = null
                ),
                UserEntity(
                    userId = "user4",
                    username = "Emily Brown",
                    isOnline = true,
                    avatarUrl = null
                )
            )
            
            userDao.insertUsers(sampleUsers)
        }
        
        // Add user memberships for this channel
        val memberships = listOf(
            UserChannelMembership(
                userId = "user1",
                channelId = channelId,
                joinedAt = System.currentTimeMillis() - 86400000 * 7, // 7 days ago
                role = "admin"
            ),
            UserChannelMembership(
                userId = "user2",
                channelId = channelId,
                joinedAt = System.currentTimeMillis() - 86400000 * 6, // 6 days ago
                role = "member"
            ),
            UserChannelMembership(
                userId = "user3",
                channelId = channelId,
                joinedAt = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
                role = "member"
            ),
            UserChannelMembership(
                userId = "user4",
                channelId = channelId,
                joinedAt = System.currentTimeMillis() - 86400000 * 3, // 3 days ago
                role = "member"
            )
        )
        
        userChannelMembershipDao.insertMemberships(memberships)
    }
} 