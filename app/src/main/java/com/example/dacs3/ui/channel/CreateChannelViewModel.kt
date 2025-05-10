package com.example.dacs3.ui.channel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.ChannelDao
import com.example.dacs3.data.local.ChannelEntity
import com.example.dacs3.data.local.UserChannelMembership
import com.example.dacs3.data.local.UserChannelMembershipDao
import com.example.dacs3.data.local.UserDao
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateChannelViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val userChannelMembershipDao: UserChannelMembershipDao,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
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
        val currentUserId = sessionManager.getUserId() ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Verify the user exists first
                val currentUser = userDao.getUserById(currentUserId)
                if (currentUser == null) {
                    _error.value = "User not found. Please log in again."
                    _isLoading.value = false
                    return@launch
                }
                
                Log.d("CreateChannel", "Creating channel with workspace ID: $workspaceId by user: $currentUserId")
                
                // Create a new channel
                val channelId = UUID.randomUUID().toString()
                
                val channel = ChannelEntity(
                    channelId = channelId,
                    name = name,
                    description = description,
                    workspaceId = workspaceId,
                    createdBy = currentUserId,
                    isPrivate = isPrivate,
                    unreadCount = 0
                )
                
                channelDao.insertChannel(channel)
                
                // Small delay to ensure the channel is created before adding memberships
                kotlinx.coroutines.delay(100)
                
                // Add the creator as a member
                val membership = UserChannelMembership(
                    userId = currentUserId,
                    channelId = channelId,
                    joinedAt = System.currentTimeMillis(),
                    role = "admin"
                )
                
                userChannelMembershipDao.insertMembership(membership)
                
                // Get other users from the database and add them as members
                // Only for non-private channels, all workspace members should be added
                if (!isPrivate) {
                    val users = userDao.getAllUsersSync()
                    val otherMemberships = users
                        .filter { it.userId != currentUserId }
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
                }
                
                _isLoading.value = false
                onComplete(channelId)
            } catch (e: Exception) {
                Log.e("CreateChannel", "Error creating channel", e)
                _error.value = "Failed to create channel: ${e.message}"
                _isLoading.value = false
            }
        }
    }
} 