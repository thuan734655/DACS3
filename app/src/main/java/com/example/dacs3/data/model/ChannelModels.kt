package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.AddMemberRequest
import com.example.dacs3.data.model.MessageResponse

// Models
data class Channel(
    val _id: String,
    val name: String,
    val description: String?,
    val workspace_id: Workspace,
    val created_by: User,
    val is_private: Boolean,
    val members: List<ChannelMember>,
    val last_message_id: String?,
    val last_message_preview: String?,
    val last_message_at: Date?,
    val created_at: Date,
    val updated_at: Date
)

data class ChannelMember(
    val user: User,
    val last_read: Date?,
    val joined_at: Date,
    val _id: String? = null
)

// Requests
data class CreateChannelRequest(
    val name: String,
    val description: String?,
    val workspace_id: String,
    val created_by: String,
    val is_private: Boolean
)

data class UpdateChannelRequest(
    val name: String?,
    val description: String?,
    val is_private: Boolean?
)

// Responses
data class ChannelResponse(
    val success: Boolean,
    val data: Channel?
)

// Thêm vào cuối file nếu chưa có
data class ChannelListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Channel>
)

// Thêm class ChannelList
data class ChannelList(
    val count: Int,
    val total: Int,
    val channels: List<Channel>
)

// MessageResponse is imported from CommonModels.kt