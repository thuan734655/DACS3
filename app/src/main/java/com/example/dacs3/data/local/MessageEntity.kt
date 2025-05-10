package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChannelEntity::class,
            parentColumns = ["channelId"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["messageId"],
            childColumns = ["threadParentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("senderId"),
        Index("receiverId"),
        Index("channelId"),
        Index("threadParentId")
    ]
)
data class MessageEntity(
    @PrimaryKey
    val messageId: String,
    val content: String,
    val fileUrl: String? = null,
    val senderId: String,
    val receiverId: String? = null,  // Null for channel messages
    val channelId: String? = null,   // Null for direct messages
    val timestamp: Long = System.currentTimeMillis(),
    val threadParentId: String? = null, // Null if not part of a thread
    val isRead: Boolean = false,
    val mentionedUser: String? = null // For @mentions
) 