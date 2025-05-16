package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.ChannelMessageApi
import com.example.dacs3.data.api.DirectMessageApi
import com.example.dacs3.data.api.MessageApi
import com.example.dacs3.data.local.dao.MessageDao
import com.example.dacs3.data.local.entity.MessageEntity
import com.example.dacs3.data.model.CreateMessageRequest
import com.example.dacs3.data.model.Message
import com.example.dacs3.data.model.MessageDataResponse
import com.example.dacs3.data.model.MessageListResponse
import com.example.dacs3.data.model.MessageResponse
import com.example.dacs3.data.model.SendChannelMessageRequest
import com.example.dacs3.data.model.SendDirectMessageRequest
import com.example.dacs3.data.model.ThreadRepliesData
import com.example.dacs3.data.model.ThreadRepliesResponse
import com.example.dacs3.data.model.UpdateMessageRequest
import com.example.dacs3.data.repository.MessageRepository
import com.example.dacs3.di.IoDispatcher
import com.example.dacs3.data.user.UserManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val messageApi: MessageApi,
    private val channelMessageApi: ChannelMessageApi,
    private val directMessageApi: DirectMessageApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MessageRepository {
    
    private val TAG = "MessageRepositoryImpl"
    
    override fun getAll(): Flow<List<MessageEntity>> {
        return messageDao.getAllMessages()
    }
    
    override suspend fun getById(id: String): MessageEntity? {
        return messageDao.getMessageById(id)
    }
    
    override suspend fun insert(item: MessageEntity) {
        messageDao.insertMessage(item)
    }
    
    override suspend fun insertAll(items: List<MessageEntity>) {
        messageDao.insertMessages(items)
    }
    
    override suspend fun update(item: MessageEntity) {
        messageDao.updateMessage(item)
    }
    
    override suspend fun delete(item: MessageEntity) {
        messageDao.deleteMessage(item)
    }
    
    override suspend fun deleteById(id: String) {
        messageDao.deleteMessageById(id)
    }
    
    override suspend fun deleteAll() {
        messageDao.deleteAllMessages()
    }
    
    override suspend fun sync() {
        try {
            val response = messageApi.getAllMessages()
            if (response.success && response.data != null) {
                val messages = response.data.map { MessageEntity.fromMessage(it) }
                messageDao.insertMessages(messages)
                Log.d(TAG, "Successfully synced ${messages.size} messages")
            } else {
                Log.w(TAG, "Failed to sync messages")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing messages", e)
        }
    }
    
    override fun getMessagesByChannelId(channelId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesByChannelId(channelId)
    }
    
    override fun getMessagesBySenderId(senderId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesBySenderId(senderId)
    }
    
    override fun getMessagesByReceiverId(receiverId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesByReceiverId(receiverId)
    }
    
    override fun getMessagesByThreadParentId(threadParentId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesByThreadParentId(threadParentId)
    }
    
    override fun getDirectMessagesBetweenUsers(userId: String, otherUserId: String): Flow<List<MessageEntity>> {
        return messageDao.getDirectMessagesBetweenUsers(userId, otherUserId)
    }
    
    override suspend fun getAllMessagesFromApi(
        page: Int?,
        limit: Int?,
        channelId: String?,
        senderId: String?,
        receiverId: String?,
        threadParentId: String?
    ): MessageListResponse {
        return try {
            val response = messageApi.getAllMessages(
                page, limit, channelId, senderId, receiverId, threadParentId
            )
            
            // If successful, store messages in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val messageEntities = response.data.map { MessageEntity.fromMessage(it) }
                    messageDao.insertMessages(messageEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching messages from API", e)
            // Return empty response with success=false when API fails
            MessageListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getMessageByIdFromApi(id: String): MessageDataResponse {
        return try {
            val messageResponse = messageApi.getMessageById(id)
            
            // Since MessageResponse from the API has a success and String message field,
            // we need to retrieve the actual Message object separately
            if (messageResponse.success) {
                // Here we should make another call to get the actual Message object
                // For now, we just create a dummy Message since we don't have enough info
                val messageEntity = getById(id)
                val message = messageEntity?.toMessage()
                
                // If we have the message in local DB, return it
                if (message != null) {
                    MessageDataResponse(true, message)
                } else {
                    // Otherwise create a placeholder Message object
                    val placeholderMessage = Message(
                        _id = id,
                        type_message = "",
                        reciver_id = null,
                        channel_id = null,
                        sender_id = "",
                        content = messageResponse.message, // Use the response message as content
                        type = "",
                        file_url = null,
                        thread_parent_id = null,
                        created_at = Date(),
                        updated_at = Date()
                    )
                    MessageDataResponse(true, placeholderMessage)
                }
            } else {
                MessageDataResponse(false, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching message from API", e)
            // Return empty response with success=false when API fails
            MessageDataResponse(false, null)
        }
    }
    
    override suspend fun getChannelMessages(
        channelId: String,
        page: Int?,
        limit: Int?
    ): MessageListResponse {
        return try {
            val response = channelMessageApi.getChannelMessages(channelId, page, limit)
            
            // If successful, store messages in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val messageEntities = response.data.map { MessageEntity.fromMessage(it) }
                    messageDao.insertMessages(messageEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching channel messages from API", e)
            // Return empty response with success=false when API fails
            MessageListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun sendChannelMessage(
        channelId: String,
        content: String?,
        type: String?,
        fileUrl: String?,
        threadParentId: String?
    ): MessageDataResponse {
        return try {
            val request = SendChannelMessageRequest(content, type, fileUrl, threadParentId)
            val messageResponse = channelMessageApi.sendChannelMessage(channelId, request)
            
            // For a successful API call, we need to fetch the actual Message
            // For now, we're returning a placeholder response
            if (messageResponse.success) {
                // In a real implementation, we would fetch the created message
                // For now, we just indicate success without the message details
                MessageDataResponse(true, null)
            } else {
                MessageDataResponse(false, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending channel message", e)
            // Return empty response with success=false when API fails
            MessageDataResponse(false, null)
        }
    }
    
    override suspend fun getChannelThreadReplies(
        messageId: String,
        page: Int?,
        limit: Int?
    ): ThreadRepliesResponse {
        return try {
            val response = channelMessageApi.getChannelThreadReplies(messageId, page, limit)
            
            // If successful, store thread and replies in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    // Store parent message
                    val parentEntity = MessageEntity.fromMessage(response.data.parent)
                    messageDao.insertMessage(parentEntity)
                    
                    // Store all replies
                    val replyEntities = response.data.replies.map { MessageEntity.fromMessage(it) }
                    messageDao.insertMessages(replyEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching channel thread replies from API", e)
            // Return empty response with success=false when API fails
            // Create a fake parent message to avoid null issues
            val fakeParentMessage = Message(
                _id = "", type_message = "", reciver_id = null,
                channel_id = null, sender_id = "", content = null,
                type = "", file_url = null, thread_parent_id = null,
                created_at = Date(), updated_at = Date()
            )
            val emptyThreadData = ThreadRepliesData(fakeParentMessage, emptyList())
            ThreadRepliesResponse(false, 0, 0, emptyThreadData)
        }
    }
    
    override suspend fun replyToChannelThread(
        messageId: String,
        content: String?,
        type: String?,
        fileUrl: String?
    ): MessageDataResponse {
        return try {
            val request = SendChannelMessageRequest(content, type, fileUrl, null)
            val messageResponse = channelMessageApi.replyToChannelThread(messageId, request)
            
            // For a successful API call, we need to fetch the actual Message
            // For now, we're returning a placeholder response
            if (messageResponse.success) {
                // In a real implementation, we would fetch the created message
                // For now, we just indicate success without the message details
                MessageDataResponse(true, null)
            } else {
                MessageDataResponse(false, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error replying to channel thread", e)
            // Return empty response with success=false when API fails
            MessageDataResponse(false, null)
        }
    }
    
    override suspend fun getDirectMessages(
        userId: String,
        page: Int?,
        limit: Int?
    ): MessageListResponse {
        return try {
            val response = directMessageApi.getMessages(userId, page, limit)
            
            // If successful, store messages in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val messageEntities = response.data.map { MessageEntity.fromMessage(it) }
                    messageDao.insertMessages(messageEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching direct messages from API", e)
            // Return empty response with success=false when API fails
            MessageListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun sendDirectMessage(
        userId: String,
        content: String?,
        type: String?,
        fileUrl: String?,
        threadParentId: String?
    ): MessageDataResponse {
        return try {
            val request = SendDirectMessageRequest(content, type, fileUrl, threadParentId)
            val messageResponse = directMessageApi.sendMessage(userId, request)
            
            // For a successful API call, we need to fetch the actual Message
            // For now, we're returning a placeholder response
            if (messageResponse.success) {
                // In a real implementation, we would fetch the created message
                // For now, we just indicate success without the message details
                MessageDataResponse(true, null)
            } else {
                MessageDataResponse(false, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending direct message", e)
            // Return empty response with success=false when API fails
            MessageDataResponse(false, null)
        }
    }
    
    override suspend fun getDirectThreadReplies(
        messageId: String,
        page: Int?,
        limit: Int?
    ): ThreadRepliesResponse {
        return try {
            val response = directMessageApi.getThreadReplies(messageId, page, limit)
            
            // If successful, store thread and replies in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    // Store parent message
                    val parentEntity = MessageEntity.fromMessage(response.data.parent)
                    messageDao.insertMessage(parentEntity)
                    
                    // Store all replies
                    val replyEntities = response.data.replies.map { MessageEntity.fromMessage(it) }
                    messageDao.insertMessages(replyEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching direct thread replies from API", e)
            // Return empty response with success=false when API fails
            // Create a fake parent message to avoid null issues
            val fakeParentMessage = Message(
                _id = "", type_message = "", reciver_id = null,
                channel_id = null, sender_id = "", content = null,
                type = "", file_url = null, thread_parent_id = null,
                created_at = Date(), updated_at = Date()
            )
            val emptyThreadData = ThreadRepliesData(fakeParentMessage, emptyList())
            ThreadRepliesResponse(false, 0, 0, emptyThreadData)
        }
    }
    
    override suspend fun replyToDirectThread(
        messageId: String,
        content: String?,
        type: String?,
        fileUrl: String?
    ): MessageDataResponse {
        return try {
            val request = SendDirectMessageRequest(content, type, fileUrl, null)
            val messageResponse = directMessageApi.replyToThread(messageId, request)
            
            // For a successful API call, we need to fetch the actual Message
            // For now, we're returning a placeholder response
            if (messageResponse.success) {
                // In a real implementation, we would fetch the created message
                // For now, we just indicate success without the message details
                MessageDataResponse(true, null)
            } else {
                MessageDataResponse(false, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error replying to direct thread", e)
            // Return empty response with success=false when API fails
            MessageDataResponse(false, null)
        }
    }
    
    override suspend fun updateMessage(
        id: String,
        content: String
    ): MessageDataResponse {
        return try {
            val request = UpdateMessageRequest(content)
            val messageResponse = messageApi.updateMessage(id, request)
            
            // For a successful API call, we need to fetch the actual Message
            // For now, we're returning a placeholder response
            if (messageResponse.success) {
                // In a real implementation, we would fetch the updated message
                // For now, we just indicate success without the message details
                MessageDataResponse(true, null)
            } else {
                MessageDataResponse(false, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating message", e)
            // Return empty response with success=false when API fails
            MessageDataResponse(false, null)
        }
    }
    
    override suspend fun deleteMessageFromApi(id: String): Boolean {
        return try {
            val response = messageApi.deleteMessage(id)
            
            // If successful, delete message from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    messageDao.deleteMessageById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting message", e)
            false
        }
    }
} 