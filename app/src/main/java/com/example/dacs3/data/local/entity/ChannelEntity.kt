package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.ChannelMember
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.Workspace
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey
    val _id: String,
    val name: String,
    val description: String?,
    val workspace_id: String,
    val created_by: String,
    val is_private: Boolean,
    val members: String,
    val last_message_id: String?,
    val last_message_preview: String?,
    val last_message_at: Long?,
    val created_at: Long,
    val updated_at: Long
) {
    fun toChannel(): Channel {
        val gson = Gson()
        return Channel(
            _id = _id,
            name = name,
            description = description,
            workspace_id = gson.fromJson(workspace_id, Workspace::class.java),
            created_by = gson.fromJson(created_by, User::class.java),
            is_private = is_private,
            members = gson.fromJson(members, object : TypeToken<List<ChannelMember>>() {}.type),
            last_message_id = last_message_id,
            last_message_preview = last_message_preview,
            last_message_at = last_message_at?.let { Date(it) },
            created_at = Date(created_at),
            updated_at = Date(updated_at)
        )
    }

    companion object {
        fun fromChannel(channel: Channel): ChannelEntity {
            val gson = Gson()
            return ChannelEntity(
                _id = channel._id,
                name = channel.name,
                description = channel.description,
                workspace_id = gson.toJson(channel.workspace_id),
                created_by = gson.toJson(channel.created_by),
                is_private = channel.is_private,
                members = gson.toJson(channel.members),
                last_message_id = channel.last_message_id,
                last_message_preview = channel.last_message_preview,
                last_message_at = channel.last_message_at?.time,
                created_at = channel.created_at.time,
                updated_at = channel.updated_at.time
            )
        }
    }
}