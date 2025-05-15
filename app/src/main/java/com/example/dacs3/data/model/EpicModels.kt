package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse

// Models
data class Epic(
    val _id: String,
    val title: String,
    val description: String?,
    val workspace_id: String,
    val created_by: String,
    val assigned_to: String?,
    val status: String,
    val priority: String,
    val start_date: Date?,
    val due_date: Date?,
    val completed_date: Date?,
    val sprint_id: String?,
    val tasks: List<String>?,
    val created_at: Date,
    val updated_at: Date
)

// Requests
data class CreateEpicRequest(
    val title: String,
    val description: String?,
    val workspace_id: String,
    val assigned_to: String?,
    val status: String?,
    val priority: String?,
    val start_date: Date?,
    val due_date: Date?,
    val sprint_id: String?
)

data class UpdateEpicRequest(
    val title: String?,
    val description: String?,
    val assigned_to: String?,
    val status: String?,
    val priority: String?,
    val start_date: Date?,
    val due_date: Date?,
    val completed_date: Date?,
    val sprint_id: String?
)

// Responses
data class EpicResponse(
    val success: Boolean,
    val data: Epic?
)

data class EpicListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Epic>
)

// MessageResponse is imported from CommonModels.kt