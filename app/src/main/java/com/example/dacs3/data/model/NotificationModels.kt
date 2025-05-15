package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse

// Models
data class Notification(
    val _id: String,
    val user_id: String,
    val type: String,
    val type_id: String?,
    val workspace_id: String,
    val content: String,
    val related_id: String?,
    val is_read: Boolean,
    val created_at: Date
)

// Requests
data class CreateNotificationRequest(
    val user_id: String,
    val type: String,
    val type_id: String?,
    val workspace_id: String,
    val content: String,
    val related_id: String?
)

data class MarkAllAsReadRequest(
    val workspaceId: String?
)

// Responses
data class NotificationResponse(
    val success: Boolean,
    val data: Notification?
)

data class NotificationListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Notification>
)

// MessageResponse is imported from CommonModels.kt