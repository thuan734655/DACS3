package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DirectMessageListResponse(
    val success: Boolean = false,
    val count: Int = 0,
    val total: Int = 0,
    val data: List<DirectMessage> = emptyList()
)

data class DirectMessageResponse(
    val success: Boolean = false,
    val data: DirectMessage? = null
)

data class DirectMessage(
    @SerializedName("_id") val _id: String = "",
    val content: String = "",
    @SerializedName("sender_id") val senderId: User? = null,
    @SerializedName("reciver_id") val receiverId: User? = null,  // Note: API uses "reciver_id" (typo in API)
    @SerializedName("type_message") val typeMessage: String = "direct",
    @SerializedName("created_at") val createdAt: Date = Date(),
    @SerializedName("updated_at") val updatedAt: Date = Date(),
    val attachments: List<Attachment>? = null,
    val reactions: List<Reaction>? = null,
    val is_edited: Boolean = false,
    val is_deleted: Boolean = false
)

data class DirectMessageRequest(
    @SerializedName("content") val content: String,
    @SerializedName("reciver_id") val receiverId: String,  // Note: API uses "reciver_id" (typo in API)
    @SerializedName("type_message") val typeMessage: String = "direct"
)

data class ChatMemberListResponse(
    val success: Boolean = false,
    val count: Int = 0,
    val total: Int = 0,
    val data: List<ChatMember> = emptyList(),
    val pagination: Pagination? = null
)

data class ChatMember(
    @SerializedName("_id") val _id: String = "",
    val user: User? = null,
    val roles: List<String> = emptyList(),
    val workspaceCount: Int = 0
)
