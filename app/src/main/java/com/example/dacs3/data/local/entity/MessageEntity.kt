package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Message
import java.util.Date

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val _id: String,
    val type_message: String,
    val reciver_id: String?,
    val channel_id: String?,
    val sender_id: String,
    val content: String?,
    val type: String,
    val file_url: String?,
    val thread_parent_id: String?,
    val created_at: Date,
    val updated_at: Date
) {
    fun toMessage(): Message {
        return Message(
            _id = _id,
            type_message = type_message,
            reciver_id = reciver_id,
            channel_id = channel_id,
            sender_id = sender_id,
            content = content,
            type = type,
            file_url = file_url,
            thread_parent_id = thread_parent_id,
            created_at = created_at,
            updated_at = updated_at
        )
    }

    companion object {
        fun fromMessage(message: Message): MessageEntity {
            return MessageEntity(
                _id = message._id,
                type_message = message.type_message,
                reciver_id = message.reciver_id,
                channel_id = message.channel_id,
                sender_id = message.sender_id,
                content = message.content,
                type = message.type,
                file_url = message.file_url,
                thread_parent_id = message.thread_parent_id,
                created_at = message.created_at,
                updated_at = message.updated_at
            )
        }
    }
} 