package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.ChatApi
import com.example.dacs3.data.model.ChatMember
import com.example.dacs3.data.model.DirectMessage
import com.example.dacs3.data.model.DirectMessageRequest
import com.example.dacs3.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {

    override suspend fun getUsersInSameWorkspaces(page: Int, limit: Int): Result<List<ChatMember>> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getUsersInSameWorkspaces(page, limit)
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to fetch workspace members"))
            }
        } catch (e: Exception) {
            Log.e("ChatRepositoryImpl", "Error fetching workspace members", e)
            Result.failure(e)
        }
    }

    override suspend fun getDirectMessages(
        senderId: String,
        receiverId: String,
        page: Int,
        limit: Int
    ): Result<List<DirectMessage>> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getDirectMessages(senderId, receiverId, page, limit)
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to fetch direct messages"))
            }
        } catch (e: Exception) {
            Log.e("ChatRepositoryImpl", "Error fetching direct messages", e)
            Result.failure(e)
        }
    }

    override suspend fun sendDirectMessage(
        receiverId: String,
        content: String
    ): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val message = DirectMessageRequest(
                content = content,
                receiverId = receiverId
            )
            val response = chatApi.sendDirectMessage(message)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to send direct message"))
            }
        } catch (e: Exception) {
            Log.e("ChatRepositoryImpl", "Error sending direct message", e)
            Result.failure(e)
        }
    }
}
