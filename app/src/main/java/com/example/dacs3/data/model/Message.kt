package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Message(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("channel_id")
    val channelId: String,
    
    @SerializedName("sender_id")
    val senderId: String,
    
    @SerializedName("content")
    val content: String? = null,
    
    @SerializedName("type")
    val type: String = "text",
    
    @SerializedName("file_url")
    val fileUrl: String? = null,
    
    @SerializedName("mentions")
    val mentions: List<String>? = null,
    
    @SerializedName("reactions")
    val reactions: List<MessageReaction>? = null,
    
    @SerializedName("thread_parent_id")
    val threadParentId: String? = null,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Date? = null
)

data class MessageReaction(
    @SerializedName("emoji")
    val emoji: String,
    
    @SerializedName("user_ids")
    val userIds: List<String>
) 