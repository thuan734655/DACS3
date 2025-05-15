package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.ChannelEntity
import com.example.dacs3.data.model.ChannelListResponse
import com.example.dacs3.data.model.ChannelResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface ChannelRepository : BaseRepository<ChannelEntity, String> {
    /**
     * Get channels by workspace ID from local database
     */
    fun getChannelsByWorkspaceId(workspaceId: String): Flow<List<ChannelEntity>>
    
    /**
     * Get channels by created by from local database
     */
    fun getChannelsByCreatedBy(createdBy: String): Flow<List<ChannelEntity>>
    
    /**
     * Get all channels from remote API with pagination and filters
     */
    suspend fun getAllChannelsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null
    ): ChannelListResponse
    
    /**
     * Get channel by ID from remote API
     */
    suspend fun getChannelByIdFromApi(id: String): ChannelResponse
    
    /**
     * Create a new channel on the remote API
     */
    suspend fun createChannel(
        name: String,
        description: String?,
        workspaceId: String,
        createdBy: String,
        isPrivate: Boolean
    ): ChannelResponse
    
    /**
     * Update a channel on the remote API
     */
    suspend fun updateChannel(
        id: String,
        name: String?,
        description: String?,
        isPrivate: Boolean?
    ): ChannelResponse
    
    /**
     * Delete a channel on the remote API
     */
    suspend fun deleteChannelFromApi(id: String): Boolean
    
    /**
     * Add a member to a channel
     */
    suspend fun addMember(channelId: String, userId: String, role: String? = null): ChannelResponse
    
    /**
     * Remove a member from a channel
     */
    suspend fun removeMember(channelId: String, userId: String): ChannelResponse
    
    /**
     * Join a channel
     */
    suspend fun joinChannel(channelId: String): ChannelResponse
    
    /**
     * Leave a channel
     */
    suspend fun leaveChannel(channelId: String): Boolean
} 