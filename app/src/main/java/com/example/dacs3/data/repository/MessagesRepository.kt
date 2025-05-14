package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.MessagesApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Message
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepository @Inject constructor(
    private val api: MessagesApi
) {
    suspend fun getChannelMessages(
        channelId: String,
        page: Int? = null,
        limit: Int? = null
    ): Response<ApiResponse<List<Message>>> {
        return try {
            api.getChannelMessages(channelId, page, limit)
        } catch (e: Exception) {
            Log.e("MessagesRepository", "Error getting channel messages", e)
            throw e
        }
    }
    
    suspend fun getDirectMessages(
        userId: String,
        page: Int? = null,
        limit: Int? = null
    ): Response<ApiResponse<List<Message>>> {
        return try {
            api.getDirectMessages(userId, page, limit)
        } catch (e: Exception) {
            Log.e("MessagesRepository", "Error getting direct messages", e)
            throw e
        }
    }
    
    suspend fun sendChannelMessage(channelId: String, message: Message): Response<ApiResponse<Message>> {
        return try {
            api.sendChannelMessage(channelId, message)
        } catch (e: Exception) {
            Log.e("MessagesRepository", "Error sending channel message", e)
            throw e
        }
    }
    
    suspend fun sendDirectMessage(userId: String, message: Message): Response<ApiResponse<Message>> {
        return try {
            api.sendDirectMessage(userId, message)
        } catch (e: Exception) {
            Log.e("MessagesRepository", "Error sending direct message", e)
            throw e
        }
    }
    
    suspend fun deleteMessage(messageId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteMessage(messageId)
        } catch (e: Exception) {
            Log.e("MessagesRepository", "Error deleting message", e)
            throw e
        }
    }
    
    suspend fun editMessage(messageId: String, message: Message): Response<ApiResponse<Message>> {
        return try {
            api.editMessage(messageId, message)
        } catch (e: Exception) {
            Log.e("MessagesRepository", "Error editing message", e)
            throw e
        }
    }
} 