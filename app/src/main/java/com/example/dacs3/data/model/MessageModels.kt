package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model đại diện cho tin nhắn (hỗ trợ cả tin nhắn channel và tin nhắn trực tiếp)
 */
data class Message(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("user_id")
    val userId: String? = null,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("timestamp")
    val timestamp: Long,
    
    @SerializedName("receiver_id")
    val receiverId: String? = null,
    
    @SerializedName("channel_id")
    val channelId: String? = null,
    
    @SerializedName("thread_parent_id")
    val threadParentId: String? = null,
    
    @SerializedName("type")
    val type: String = "channel", // "channel" hoặc "direct"
    
    @SerializedName("attachments")
    val attachments: List<Attachment>? = null,
    
    @SerializedName("reactions")
    val reactions: List<Reaction>? = null,
    
    // Các trường có thể được nạp từ populate
    @SerializedName("user")
    val user: User? = null
)

/**
 * Model đại diện cho tệp đính kèm (hình ảnh, tài liệu, v.v.)
 */
data class Attachment(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("file_name")
    val fileName: String,
    
    @SerializedName("file_url")
    val fileUrl: String,
    
    @SerializedName("file_type")
    val fileType: String,
    
    @SerializedName("file_size")
    val fileSize: Long
)

/**
 * Model đại diện cho phản ứng (emoji) đối với tin nhắn
 */
data class Reaction(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("emoji")
    val emoji: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("timestamp")
    val timestamp: Long
)

/**
 * Model đại diện cho phản hồi từ API khi tạo tin nhắn
 */
data class MessageDetailResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: Message? = null
)

/**
 * Model đại diện cho phản hồi danh sách tin nhắn từ API
 */
data class MessageListResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("pages")
    val pages: Int,
    
    @SerializedName("data")
    val data: List<Message>
)
