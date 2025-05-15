package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.Comment
import com.example.dacs3.data.model.CommentResponse
import com.example.dacs3.data.model.AddCommentRequest
import com.example.dacs3.data.model.MessageResponse

// Models
data class Task(
    val _id: String,
    val title: String,
    val description: String?,
    val workspace_id: String,
    val epic_id: String?,
    val created_by: String,
    val assigned_to: String?,
    val status: String,
    val priority: String,
    val estimated_hours: Number,
    val spent_hours: Number,
    val start_date: Date?,
    val due_date: Date?,
    val completed_date: Date?,
    val sprint_id: String?,
    val comments: List<Comment>?,
    val attachments: List<Attachment>?,
    val created_at: Date,
    val updated_at: Date
)

// Comment is imported from CommonModels.kt

data class Attachment(
    val file_name: String,
    val file_url: String,
    val uploaded_by: String,
    val uploaded_at: Date
)

// Requests
data class CreateTaskRequest(
    val title: String,
    val description: String?,
    val workspace_id: String,
    val epic_id: String?,
    val assigned_to: String?,
    val status: String?,
    val priority: String?,
    val estimated_hours: Number?,
    val spent_hours: Number?,
    val start_date: Date?,
    val due_date: Date?,
    val sprint_id: String?
)

data class UpdateTaskRequest(
    val title: String?,
    val description: String?,
    val epic_id: String?,
    val assigned_to: String?,
    val status: String?,
    val priority: String?,
    val estimated_hours: Number?,
    val spent_hours: Number?,
    val start_date: Date?,
    val due_date: Date?,
    val completed_date: Date?,
    val sprint_id: String?
)

// AddCommentRequest is imported from CommonModels.kt

// Responses
data class TaskResponse(
    val success: Boolean,
    val data: Task?
)

data class TaskListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Task>
)

// CommentResponse is imported from CommonModels.kt// MessageResponse is imported from CommonModels.kt