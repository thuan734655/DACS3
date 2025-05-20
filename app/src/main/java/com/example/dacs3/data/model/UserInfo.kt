package com.example.dacs3.data.model

/**
 * Simplified user information used in Task model
 */
data class UserInfo(
    val _id: String,
    val name: String,
    val avatar: String? = null,
    val created_at: String? = null,
    val __v: Int = 0
)
