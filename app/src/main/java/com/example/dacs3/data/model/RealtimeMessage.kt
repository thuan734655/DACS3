package com.example.dacs3.data.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.Date

/**
 * Model class for real-time messages in Firebase Realtime Database
 */
@IgnoreExtraProperties
data class RealtimeMessage(
    val id: String = "", // Unique message ID
    val workspaceId: String = "", // Workspace where the message belongs
    val senderId: String = "", // User ID of sender
    val senderName: String = "", // Name of sender
    val content: String = "", // Message content
    val timestamp: Long = 0, // Timestamp when message was sent
    val read: Boolean = false // Whether message has been read
) {
    // Empty constructor required for Firebase
    constructor() : this("", "", "", "", "", 0, false)
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "workspaceId" to workspaceId,
            "senderId" to senderId,
            "senderName" to senderName,
            "content" to content,
            "timestamp" to timestamp,
            "read" to read
        )
    }
}

/**
 * Model class for conversation metadata
 */
@IgnoreExtraProperties
data class Conversation(
    val id: String = "", // Conversation ID (typically workspaceId)
    val name: String = "", // Name of the conversation (workspace name)
    val lastMessage: String = "", // Last message content
    val lastMessageTime: Long = 0, // Timestamp of last message
    val participants: Map<String, Boolean> = emptyMap() // Map of user IDs who are part of this conversation
) {
    // Empty constructor required for Firebase
    constructor() : this("", "", "", 0, emptyMap())
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "lastMessage" to lastMessage,
            "lastMessageTime" to lastMessageTime,
            "participants" to participants
        )
    }
}
