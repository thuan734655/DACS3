package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse
import com.example.dacs3.data.model.AddMemberRequest

// Models
data class Workspace(
    val _id: String,
    val name: String,
    val description: String?,
    val created_by: User,
    val created_at: Date,
    val members: List<WorkspaceMember>?,
    val channels: List<String>?
)

data class WorkspaceMember(
    val user_id: User,
    val role: String,
    val _id: String? = null
)

// Requests
data class CreateWorkspaceRequest(
    val name: String,
    val description: String?
)

data class UpdateWorkspaceRequest(
    val name: String?,
    val description: String?
)

// AddMemberRequest is imported from CommonModels.kt

data class UpdateMemberRoleRequest(
    val role: String
)

// Responses
data class WorkspaceResponse(
    val success: Boolean,
    val data: Workspace?
)

data class WorkspaceListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<Workspace>
)

// MessageResponse is imported from CommonModels.kt