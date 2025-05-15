package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Notification
import java.util.Date

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val _id: String,
    val user_id: String,
    val type: String,
    val type_id: String?,
    val workspace_id: String,
    val content: String,
    val related_id: String?,
    val is_read: Boolean,
    val created_at: Date
) {
    fun toNotification(): Notification {
        return Notification(
            _id = _id,
            user_id = user_id,
            type = type,
            type_id = type_id,
            workspace_id = workspace_id,
            content = content,
            related_id = related_id,
            is_read = is_read,
            created_at = created_at
        )
    }

    companion object {
        fun fromNotification(notification: Notification): NotificationEntity {
            return NotificationEntity(
                _id = notification._id,
                user_id = notification.user_id,
                type = notification.type,
                type_id = notification.type_id,
                workspace_id = notification.workspace_id,
                content = notification.content,
                related_id = notification.related_id,
                is_read = notification.is_read,
                created_at = notification.created_at
            )
        }
    }
} 