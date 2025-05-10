package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("senderId"),
        Index("receiverId")
    ]
)
data class NotificationEntity(
    @PrimaryKey val notificationId: String,
    val content: String,
    val senderId: String,
    val receiverId: String,
    val linkTo: String?, // URL or deeplink
    val type: String, // e.g., "message", "invite", "task_assignment"
    val status: NotificationStatus = NotificationStatus.UNREAD,
    val createdAt: Long = System.currentTimeMillis()
) 