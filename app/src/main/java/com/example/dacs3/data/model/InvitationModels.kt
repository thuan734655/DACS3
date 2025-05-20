package com.example.dacs3.data.model

import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.Workspace
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Model đại diện cho lời mời tham gia workspace
 */
data class Invitation(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("type_invitation")
    val typeInvitation: String,
    
    @SerializedName("workspace_id")
    val workspaceId: Any, // Có thể là String hoặc đối tượng Workspace
    
    @SerializedName("user_id")
    val userId: Any, // Có thể là String hoặc đối tượng User
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("invited_by")
    val invitedBy: Any, // Có thể là String hoặc đối tượng User
    
    @SerializedName("status")
    val status: String, // "pending", "accepted", "rejected"
    
    @SerializedName("created_at")
    val createdAt: Date,
    
    @SerializedName("updated_at")
    val updatedAt: Date
)

/**
 * Request gửi lời mời tham gia workspace
 */
data class SendInvitationRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("workspace_id")
    val workspaceId: String
)

/**
 * Response khi gửi lời mời thành công
 */
data class InvitationResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: Invitation? = null,
    
    @SerializedName("message")
    val message: String? = null
)

/**
 * Response khi lấy danh sách lời mời
 */
data class InvitationListResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: List<Invitation>,
    
    @SerializedName("pagination")
    val pagination: Pagination? = null
)

data class Pagination(
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("limit")
    val limit: Int,
    
    @SerializedName("totalPages")
    val totalPages: Int
)
