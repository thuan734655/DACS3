package com.example.dacs3.data.model

import java.util.Date

data class MessageResponse(
    val success: Boolean,
    val message: String
)

data class Comment(
    val user_id: String,
    val content: String,
    val created_at: Date,
    val updated_at: Date
)

data class AddCommentRequest(
    val content: String
)

data class CommentResponse(
    val success: Boolean,
    val data: Comment?
)

data class AddMemberRequest(
    val user_id: String,
    val role: String? = null
)

data class VerifyEmailRequest(
    val email: String,
    val otp: String
) 