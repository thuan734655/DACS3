package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_channel_memberships",
    primaryKeys = ["userId", "channelId"],
    indices = [
        Index("userId"),
        Index("channelId")
    ]
)
data class UserChannelMembership(
    val userId: String,
    val channelId: String,
    val joinedAt: Long,
    val role: String // e.g., "admin", "member"
) 