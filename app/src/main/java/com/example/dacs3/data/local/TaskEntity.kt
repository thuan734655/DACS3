package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("createdBy")]
)
data class TaskEntity(
    @PrimaryKey val taskId: String,
    val name: String,
    val description: String = "",
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val priority: Int = 0, // 0-5 (low to high)
    val status: Status = Status.TO_DO,
    val progress: Int = 0, // 0-100
    val assignedToUserId: String? = null,
    val epicId: String? = null
) 