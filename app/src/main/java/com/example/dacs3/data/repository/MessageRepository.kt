package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.MessageEntity
import com.example.dacs3.data.model.MessageDataResponse
import com.example.dacs3.data.model.MessageListResponse
import com.example.dacs3.data.model.ThreadRepliesResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface MessageRepository : BaseRepository<MessageEntity, String> {
    /**
     * Get messages by channel ID from local database
     */
    fun getMessagesByChannelId(channelId: String): Flow<List<MessageEntity>>
    
    /**
     * Get messages by sender ID from local database
     */
    fun getMessagesBySenderId(senderId: String): Flow<List<MessageEntity>>
    
    /**
     * Get messages by receiver ID from local database
     */
    fun getMessagesByReceiverId(receiverId: String): Flow<List<MessageEntity>>
    
    /**
     * Get messages by thread parent ID from local database
     */
    fun getMessagesByThreadParentId(threadParentId: String): Flow<List<MessageEntity>>
    
    /**
     * Get direct messages between two users from local database
     */
    fun getDirectMessagesBetweenUsers(userId: String, otherUserId: String): Flow<List<MessageEntity>>
    
    /**
     * Get all messages from remote API with pagination and filters
     */
    suspend fun getAllMessagesFromApi(
        page: Int? = null,
        limit: Int? = null,
        channelId: String? = null,
        senderId: String? = null,
        receiverId: String? = null,
        threadParentId: String? = null
    ): MessageListResponse
    
    /**
     * Get message by ID from remote API
     */
    suspend fun getMessageByIdFromApi(id: String): MessageDataResponse
    
    /**
     * Get channel messages from remote API
     */
    suspend fun getChannelMessages(
        channelId: String,
        page: Int? = null,
        limit: Int? = null
    ): MessageListResponse
    
    /**
     * Send a message to a channel
     */
    suspend fun sendChannelMessage(
        channelId: String,
        content: String?,
        type: String? = null,
        fileUrl: String? = null,
        threadParentId: String? = null
    ): MessageDataResponse
    
    /**
     * Get thread replies for a channel message
     */
    suspend fun getChannelThreadReplies(
        messageId: String,
        page: Int? = null,
        limit: Int? = null
    ): ThreadRepliesResponse
    
    /**
     * Reply to a thread in a channel
     */
    suspend fun replyToChannelThread(
        messageId: String,
        content: String?,
        type: String? = null,
        fileUrl: String? = null
    ): MessageDataResponse
    
    /**
     * Get direct messages with a specific user
     */
    suspend fun getDirectMessages(
        userId: String,
        page: Int? = null,
        limit: Int? = null
    ): MessageListResponse
    
    /**
     * Send a direct message to a user
     */
    suspend fun sendDirectMessage(
        userId: String,
        content: String?,
        type: String? = null,
        fileUrl: String? = null,
        threadParentId: String? = null
    ): MessageDataResponse
    
    /**
     * Get thread replies for a direct message
     */
    suspend fun getDirectThreadReplies(
        messageId: String,
        page: Int? = null,
        limit: Int? = null
    ): ThreadRepliesResponse
    
    /**
     * Reply to a thread in a direct message
     */
    suspend fun replyToDirectThread(
        messageId: String,
        content: String?,
        type: String? = null,
        fileUrl: String? = null
    ): MessageDataResponse
    
    /**
     * Update a message
     */
    suspend fun updateMessage(
        id: String,
        content: String
    ): MessageDataResponse
    
    /**
     * Delete a message
     */
    suspend fun deleteMessageFromApi(id: String): Boolean
} 