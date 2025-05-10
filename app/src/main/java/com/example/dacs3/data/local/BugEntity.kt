package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bugs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("createdBy"),
        Index("taskId")
    ]
)
data class BugEntity(
    @PrimaryKey val bugId: String,
    val name: String,
    val description: String,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val priority: Int = 0, // 0-5
    val status: Status = Status.TO_DO,
    val taskId: String
) 