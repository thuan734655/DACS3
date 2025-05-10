package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "workspace_user_memberships",
    primaryKeys = ["userId", "workspaceId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkspaceEntity::class,
            parentColumns = ["workspaceId"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("workspaceId")
    ]
)
data class WorkspaceUserMembership(
    val userId: String,
    val workspaceId: String,
    val joinedAt: Long = System.currentTimeMillis(),
    val role: String // admin, member
) 