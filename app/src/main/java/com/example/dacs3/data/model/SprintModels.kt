package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse

// Models
data class WorkspaceInfo(
    val _id: String,
    val name: String,
    val description: String?
)

data class UserInfo(
    val _id: String,
    val name: String,
    val avatar: String?
)

data class Sprint(
    val _id: String,
    val name: String,
    val description: String?,
    val workspace_id: WorkspaceInfo,
    val created_by: UserInfo,
    val status: String,
    val start_date: Date,
    val end_date: Date,
    val goal: String?,
    val tasks: List<String>?,
    val created_at: Date,
    val updated_at: Date
)

// Requests
data class CreateSprintRequest(
    val name: String,
    val description: String?,
    val workspace_id: String,
    val start_date: Date,
    val end_date: Date,
    val goal: String?,
    val status: String?
)

data class UpdateSprintRequest(
    val name: String?,
    val description: String?,
    val start_date: Date?,
    val end_date: Date?,
    val goal: String?,
    val status: String?
)

data class AddItemsRequest(
    val tasks: List<String>?
)

data class RemoveItemsRequest(
    val tasks: List<String>?
)

// Responses
data class SprintResponse(
    val success: Boolean,
    val data: Sprint?
)

data class SprintListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Sprint>
)

// MessageResponse is imported from CommonModels.kt