package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "epics",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["createdBy"],
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
        Index("createdBy"),
        Index("workspaceId")
    ]
)
data class EpicEntity(
    @PrimaryKey val epicId: String,
    val name: String,
    val description: String,
    val createdBy: String,
    val priority: Int = 0, // 0-5
    val status: Status = Status.TO_DO,
    val workspaceId: String
) 