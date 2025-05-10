package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
    foreignKeys = [
        ForeignKey(
            entity = WorkspaceEntity::class,
            parentColumns = ["workspaceId"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["deputyLeaderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("workspaceId"),
        Index("createdBy"),
        Index("deputyLeaderId")
    ]
)
data class ChannelEntity(
    @PrimaryKey
    val channelId: String,
    val name: String,
    val description: String = "",
    val workspaceId: String,
    val createdBy: String,
    val deputyLeaderId: String? = null,
    val isPrivate: Boolean = false,
    val unreadCount: Int = 0
) 