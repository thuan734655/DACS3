package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.ChannelEntity
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.ChannelList
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    // lấy toàn bộ (danh sách có paging)
    suspend fun getAllChannelsFromApi(
        page: Int? = null,
        limit: Int? = null
    ): ApiResponse<ChannelList>

    suspend fun getChannelsByWorkspaceFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String
    ): ApiResponse<List<Channel>>

    suspend fun getChannelsByCreatedBy(userId: String): ApiResponse<List<Channel>>
    suspend fun getChannelsByWorkspaceId(workspaceId: String): Flow<List<ChannelEntity>>
    suspend fun createChannel(
        name: String,
        description: String?,
        workspaceId: String,
        createdBy: String,
        isPrivate: Boolean = false
    ): ApiResponse<Channel>    suspend fun addMember(channelId: String, userId: String): ApiResponse<Channel>
    suspend fun joinChannel(channelId: String): ApiResponse<Channel>
    suspend fun leaveChannel(channelId: String): ApiResponse<Channel>
}