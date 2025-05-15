package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index("email", unique = true), Index("contactNumber", unique = true)]
)
data class AccountEntity(
    @PrimaryKey
    val _id: String,
    val email: String,
    val contactNumber: String,
    val password: String,
    val otp: String?,
    val create_at_otp: Date?,
    val verifyMail: Boolean,
    val deviceID: String?,
    val user_id: String
) 