package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse as CommonMessageResponse

// Models
data class Message(
    val _id: String,
    val type_message: String,
    val reciver_id: String?,
    val channel_id: String?,
    val sender_id: String,
    val content: String?,
    val type: String,
    val file_url: String?,
    val thread_parent_id: String?,
    val created_at: Date,
    val updated_at: Date
)

// Requests
data class SendChannelMessageRequest(
    val content: String?,
    val type: String?,
    val file_url: String?,
    val thread_parent_id: String?
)

data class SendDirectMessageRequest(
    val content: String?,
    val type: String?,
    val file_url: String?,
    val thread_parent_id: String?
)

data class CreateMessageRequest(
    val type_message: String,
    val reciver_id: String?,
    val channel_id: String?,
    val sender_id: String,
    val content: String?,
    val type: String?,
    val file_url: String?,
    val thread_parent_id: String?
)

data class UpdateMessageRequest(
    val content: String
)

// Responses
data class MessageDataResponse(
    val success: Boolean,
    val data: Message?
)

data class MessageListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Message>
)

data class ThreadRepliesResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: ThreadRepliesData
)

data class ThreadRepliesData(
    val parent: Message,
    val replies: List<Message>
)

data class ConversationResponse(
    val success: Boolean,
    val count: Int,
    val data: List<ConversationData>
)

data class ConversationData(
    val _id: String,
    val user: User,
    val lastMessage: Message
)