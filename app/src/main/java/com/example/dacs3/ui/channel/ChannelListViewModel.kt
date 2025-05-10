package com.example.dacs3.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.ChannelDao
import com.example.dacs3.data.local.ChannelEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val channelDao: ChannelDao
) : ViewModel() {
    
    val channels = channelDao.getAllChannels()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    
    init {
        viewModelScope.launch {
            val channelCount = channelDao.getChannelCount()
            if (channelCount == 0) {
                createSampleChannels()
            }
        }
    }
    
    private suspend fun createSampleChannels() {
        val sampleChannels = listOf(
            ChannelEntity(
                channelId = "channel1",
                name = "general",
                description = "General discussions",
                workspaceId = "workspace1",
                createdBy = "user1",
                isPrivate = false,
                unreadCount = 1
            ),
            ChannelEntity(
                channelId = "channel2",
                name = "development",
                description = "Dev team discussions",
                workspaceId = "workspace1",
                createdBy = "user1",
                isPrivate = false,
                unreadCount = 0
            ),
            ChannelEntity(
                channelId = "channel3",
                name = "design",
                description = "Design team discussions",
                workspaceId = "workspace1",
                createdBy = "user2",
                isPrivate = false,
                unreadCount = 0
            ),
            ChannelEntity(
                channelId = "channel4",
                name = "secret-project",
                description = "Confidential project discussions",
                workspaceId = "workspace2",
                createdBy = "user2",
                isPrivate = true,
                unreadCount = 3
            )
        )
        
        channelDao.insertChannels(sampleChannels)
    }
} 