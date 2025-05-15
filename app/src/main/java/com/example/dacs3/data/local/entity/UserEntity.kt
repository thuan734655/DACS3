package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.User
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val _id: String,
    val name: String,
    val avatar: String?,
    val created_at: Date
) {
    fun toUser(): User {
        return User(
            _id = _id,
            name = name,
            avatar = avatar,
            created_at = created_at
        )
    }

    companion object {
        fun fromUser(user: User): UserEntity {
            return UserEntity(
                _id = user._id,
                name = user.name,
                avatar = user.avatar,
                created_at = user.created_at
            )
        }
    }
} 