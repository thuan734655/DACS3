package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invitations",
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
        ),
        ForeignKey(
            entity = WorkspaceEntity::class,
            parentColumns = ["workspaceId"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("senderId"),
        Index("receiverId"),
        Index("workspaceId")
    ]
)
data class InvitationEntity(
    @PrimaryKey val invitationId: String,
    val senderId: String,
    val receiverId: String,
    val workspaceId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: InvitationStatus = InvitationStatus.PENDING
) 