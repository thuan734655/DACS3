package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.ChannelApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    private val api: ChannelApi
) {
    suspend fun getAllChannels(): Response<ApiResponse<List<Channel>>> {
        return try {
            api.getAllChannels()
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error getting all channels", e)
            throw e
        }
    }
    
    suspend fun getChannelById(channelId: String): Response<ApiResponse<Channel>> {
        return try {
            api.getChannelById(channelId)
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error getting channel by id", e)
            throw e
        }
    }
    
    suspend fun createChannel(channel: Channel): Response<ApiResponse<Channel>> {
        return try {
            api.createChannel(channel)
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error creating channel", e)
            throw e
        }
    }
    
    suspend fun updateChannel(channelId: String, channel: Channel): Response<ApiResponse<Channel>> {
        return try {
            api.updateChannel(channelId, channel)
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error updating channel", e)
            throw e
        }
    }
    
    suspend fun deleteChannel(channelId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteChannel(channelId)
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error deleting channel", e)
            throw e
        }
    }
    
    suspend fun addMember(channelId: String, memberId: String): Response<ApiResponse<Channel>> {
        return try {
            val memberData = mapOf("memberId" to memberId)
            api.addMember(channelId, memberData)
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error adding member to channel", e)
            throw e
        }
    }
    
    suspend fun removeMember(channelId: String, memberId: String): Response<ApiResponse<Channel>> {
        return try {
            api.removeMember(channelId, memberId)
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Error removing member from channel", e)
            throw e
        }
    }
} 