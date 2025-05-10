package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workspaces",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["leaderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("createdBy"),
        Index("leaderId")
    ]
)
data class WorkspaceEntity(
    @PrimaryKey val workspaceId: String,
    val name: String,
    val description: String,
    val createdBy: String,
    val leaderId: String,
    val startTime: Long? = System.currentTimeMillis(),
    val completeTime: Long? = null
) 