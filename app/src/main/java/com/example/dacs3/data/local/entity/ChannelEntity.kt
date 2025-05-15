package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.ChannelMember
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
    val members: List<ChannelMember>,
    val last_message_id: String?,
    val last_message_preview: String?,
    val last_message_at: Date?,
    val created_at: Date,
    val updated_at: Date
) {
    fun toChannel(): Channel {
        return Channel(
            _id = _id,
            name = name,
            description = description,
            workspace_id = workspace_id,
            created_by = created_by,
            is_private = is_private,
            members = members,
            last_message_id = last_message_id,
            last_message_preview = last_message_preview,
            last_message_at = last_message_at,
            created_at = created_at,
            updated_at = updated_at
        )
    }

    companion object {
        fun fromChannel(channel: Channel): ChannelEntity {
            return ChannelEntity(
                _id = channel._id,
                name = channel.name,
                description = channel.description,
                workspace_id = channel.workspace_id,
                created_by = channel.created_by,
                is_private = channel.is_private,
                members = channel.members,
                last_message_id = channel.last_message_id,
                last_message_preview = channel.last_message_preview,
                last_message_at = channel.last_message_at,
                created_at = channel.created_at,
                updated_at = channel.updated_at
            )
        }
    }
} 