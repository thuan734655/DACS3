package com.example.dacs3.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceDetailViewModel @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val channelDao: ChannelDao,
    private val userDao: UserDao,
    private val workspaceUserMembershipDao: WorkspaceUserMembershipDao
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    
    val workspace = _workspaceId.filterNotNull().flatMapLatest { id ->
        flow {
            val workspace = workspaceDao.getWorkspaceById(id)
            emit(workspace)
        }
    }
    
    val channels = _workspaceId.filterNotNull().flatMapLatest { id ->
        channelDao.getChannelsByWorkspaceId(id)
    }
    
    val members = _workspaceId.filterNotNull().flatMapLatest { id ->
        workspaceUserMembershipDao.getMembersByWorkspaceId(id)
            .combine(userDao.getAllUsers()) { memberships, users ->
                val memberIds = memberships.map { it.userId }
                users.filter { it.userId in memberIds }
            }
    }
    
    fun setWorkspaceId(id: String) {
        _workspaceId.value = id
        loadWorkspaceData(id)
    }
    
    private fun loadWorkspaceData(workspaceId: String) {
        viewModelScope.launch {
            // Load sample data if the database is empty
            val channelCount = channelDao.getChannelCountByWorkspaceId(workspaceId)
            if (channelCount == 0) {
                loadSampleChannels(workspaceId)
            }
            
            val memberCount = workspaceUserMembershipDao.getMemberCountByWorkspaceId(workspaceId)
            if (memberCount == 0) {
                loadSampleMembers(workspaceId)
            }
        }
    }
    
    private suspend fun loadSampleChannels(workspaceId: String) {
        val sampleChannels = listOf(
            ChannelEntity(
                channelId = "channel1$workspaceId",
                name = "general",
                description = "General workspace discussions",
                workspaceId = workspaceId,
                createdBy = "user1",
                isPrivate = false,
                unreadCount = 0
            ),
            ChannelEntity(
                channelId = "channel2$workspaceId",
                name = "development",
                description = "Dev team discussions",
                workspaceId = workspaceId,
                createdBy = "user1",
                isPrivate = false,
                unreadCount = 0
            ),
            ChannelEntity(
                channelId = "channel3$workspaceId",
                name = "design",
                description = "Design team discussions",
                workspaceId = workspaceId,
                createdBy = "user1",
                isPrivate = false,
                unreadCount = 0
            ),
            ChannelEntity(
                channelId = "channel4$workspaceId",
                name = "private",
                description = "Private discussions",
                workspaceId = workspaceId,
                createdBy = "user1",
                isPrivate = true,
                unreadCount = 0
            )
        )
        
        channelDao.insertChannels(sampleChannels)
    }
    
    private suspend fun loadSampleMembers(workspaceId: String) {
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
                ),
                UserEntity(
                    userId = "user5",
                    username = "Alex Johnson",
                    isOnline = false,
                    avatarUrl = null
                )
            )
            
            userDao.insertUsers(sampleUsers)
        }
        
        // Add user memberships for this workspace
        val memberships = listOf(
            WorkspaceUserMembership(
                workspaceId = workspaceId,
                userId = "user1",
                role = "ADMIN"
            ),
            WorkspaceUserMembership(
                workspaceId = workspaceId,
                userId = "user2",
                role = "MEMBER"
            ),
            WorkspaceUserMembership(
                workspaceId = workspaceId,
                userId = "user3",
                role = "MEMBER"
            ),
            WorkspaceUserMembership(
                workspaceId = workspaceId,
                userId = "user4",
                role = "MEMBER"
            )
        )
        
        workspaceUserMembershipDao.insertMemberships(memberships)
    }
} 