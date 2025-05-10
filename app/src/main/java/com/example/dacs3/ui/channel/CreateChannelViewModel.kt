package com.example.dacs3.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.ChannelDao
import com.example.dacs3.data.local.ChannelEntity
import com.example.dacs3.data.local.UserChannelMembership
import com.example.dacs3.data.local.UserChannelMembershipDao
import com.example.dacs3.data.local.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateChannelViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val userChannelMembershipDao: UserChannelMembershipDao,
    private val userDao: UserDao
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    
    fun setWorkspaceId(id: String) {
        _workspaceId.value = id
    }
    
    fun createChannel(
        name: String,
        description: String,
        isPrivate: Boolean,
        onComplete: (String) -> Unit
    ) {
        val workspaceId = _workspaceId.value ?: return
        
        viewModelScope.launch {
            // Create a new channel
            val channelId = UUID.randomUUID().toString()
            
            val channel = ChannelEntity(
                channelId = channelId,
                name = name,
                description = description,
                workspaceId = workspaceId,
                createdBy = "user1", // Hardcoded for now
                isPrivate = isPrivate,
                unreadCount = 0
            )
            
            channelDao.insertChannel(channel)
            
            // Add the creator as a member
            val membership = UserChannelMembership(
                userId = "user1", // Hardcoded current user ID
                channelId = channelId,
                joinedAt = System.currentTimeMillis(),
                role = "admin"
            )
            
            userChannelMembershipDao.insertMembership(membership)
            
            // Get other users from the database and add them as members
            val users = userDao.getAllUsersSync()
            val otherMemberships = users
                .filter { it.userId != "user1" }
                .map { user ->
                    UserChannelMembership(
                        userId = user.userId,
                        channelId = channelId,
                        joinedAt = System.currentTimeMillis(),
                        role = "member"
                    )
                }
            
            if (otherMemberships.isNotEmpty()) {
                userChannelMembershipDao.insertMemberships(otherMemberships)
            }
            
            onComplete(channelId)
        }
    }
} 