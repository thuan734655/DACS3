package com.example.dacs3.data.model

import java.util.Date

// User model
data class User(
    val id: String,
    val name: String,
    val avatar: String? = null
)

// Workspace model
data class Workspace(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val createdBy: User,
    val createdAt: Date,
    val members: List<WorkspaceMember> = emptyList(),
    val channels: List<String> = emptyList() // Channel IDs
)

data class WorkspaceMember(
    val userId: String,
    val role: String // "Leader" or "Member"
)

// Channel model
data class Channel(
    val id: String,
    val workspaceId: String,
    val name: String,
    val description: String? = null,
    val createdBy: String,
    val createdAt: Date,
    val members: List<String> = emptyList(),
    val isPrivate: Boolean = false,
    val hasUnread: Boolean = false
)

// Message model
data class Message(
    val id: String,
    val channelId: String,
    val senderId: String,
    val content: String? = null,
    val type: String = "text", // "text", "image", "file"
    val fileUrl: String? = null,
    val mentions: List<String> = emptyList(),
    val reactions: List<MessageReaction> = emptyList(),
    val threadParentId: String? = null,
    val createdAt: Date,
    val updatedAt: Date
)

data class MessageReaction(
    val emoji: String,
    val userIds: List<String>
)

// Notification model
data class Notification(
    val id: String,
    val userId: String,
    val type: String, // "mention", "task", "reminder", "report"
    val workspaceId: String,
    val content: String,
    val relatedId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Date
)

// HomeState đã được chuyển sang file HomeState.kt 