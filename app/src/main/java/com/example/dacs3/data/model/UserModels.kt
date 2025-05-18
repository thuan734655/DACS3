package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse

// Models
data class User(
    val _id: String,
    val name: String,
    val avatar: String?,
    val created_at: Date
)

// Requests
data class CreateUserRequest(
    val name: String,
    val avatar: String?
)

data class UpdateUserRequest(
    val name: String?,
    val avatar: String?
)

// Responses
data class UserResponse(
    val success: Boolean,
    val data: User?
)

data class ProfileResponse(val success: Boolean, val data: User?)
data class UpdateProfileRequest(val name: String, val avatar: String?)

data class UserListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<User>
)

// MessageResponse is imported from CommonModels.kt