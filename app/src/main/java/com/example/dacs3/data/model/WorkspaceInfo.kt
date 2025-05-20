package com.example.dacs3.data.model

/**
 * Simplified workspace information used in Task model
 */
data class WorkspaceInfo(
    val _id: String,
    val name: String,
    val description: String? = null,
    val created_by: String? = null,
    val members: List<Any>? = null,
    val channels: List<Any>? = null,
    val created_at: String? = null,
    val __v: Int = 0
)
