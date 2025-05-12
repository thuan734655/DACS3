package com.example.dacs3.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class AccountEntity(
    @PrimaryKey val accountId: String,
    val email: String,
    val contactNumber: String?,
    val password: String,
    val isEmailVerified: Boolean = false,
    val otp: String? = null,
    val otpCreatedAt: Long? = null,
    val deviceId: String? = null,
    val isDeviceVerified: Boolean = false,
    val userId: String
) 