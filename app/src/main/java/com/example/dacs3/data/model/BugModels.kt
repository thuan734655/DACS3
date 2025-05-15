package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.Comment
import com.example.dacs3.data.model.CommentResponse
import com.example.dacs3.data.model.AddCommentRequest
import com.example.dacs3.data.model.MessageResponse

// Models
data class Bug(
    val _id: String,
    val title: String,
    val description: String?,
    val workspace_id: String,
    val task_id: String?,
    val reported_by: String,
    val assigned_to: String?,
    val status: String,
    val completed_date: Date?,
    val priority: String,
    val comments: List<Comment>?,
    val created_at: Date,
    val updated_at: Date
)

// Comment is imported from CommonModels.kt

// Requests
data class CreateBugRequest(
    val title: String,
    val description: String?,
    val workspace_id: String,
    val task_id: String?,
    val assigned_to: String?,
    val status: String?,
    val severity: String?,
    val steps_to_reproduce: String?,
    val expected_behavior: String?,
    val actual_behavior: String?
)

data class UpdateBugRequest(
    val title: String?,
    val description: String?,
    val task_id: String?,
    val assigned_to: String?,
    val status: String?,
    val severity: String?,
    val steps_to_reproduce: String?,
    val expected_behavior: String?,
    val actual_behavior: String?
)

// AddCommentRequest is imported from CommonModels.kt

// Responses
data class BugResponse(
    val success: Boolean,
    val data: Bug?
)

data class BugListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Bug>
)

// CommentResponse is imported from CommonModels.kt

// MessageResponse is imported from CommonModels.kt