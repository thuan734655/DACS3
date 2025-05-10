package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val avatarUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false
) 