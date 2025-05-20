package com.example.dacs3.data.repository

import com.example.dacs3.data.model.ChatMember
import com.example.dacs3.data.model.DirectMessage
import com.example.dacs3.data.model.DirectMessageRequest
import com.example.dacs3.data.model.DirectMessageResponse

interface ChatRepository {
    suspend fun getUsersInSameWorkspaces(page: Int = 1, limit: Int = 20): Result<List<ChatMember>>
    suspend fun getDirectMessages(senderId: String, receiverId: String, page: Int = 1, limit: Int = 20): Result<List<DirectMessage>>
    suspend fun sendDirectMessage(receiverId: String, content: String): Result<DirectMessage>
}
