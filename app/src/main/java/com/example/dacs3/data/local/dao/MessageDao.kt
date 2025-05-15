package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE _id = :id")
    suspend fun getMessageById(id: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE channel_id = :channelId")
    fun getMessagesByChannelId(channelId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE sender_id = :senderId")
    fun getMessagesBySenderId(senderId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE reciver_id = :receiverId")
    fun getMessagesByReceiverId(receiverId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE thread_parent_id = :threadParentId")
    fun getMessagesByThreadParentId(threadParentId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE type_message = 'DIRECT' AND ((sender_id = :userId AND reciver_id = :otherUserId) OR (sender_id = :otherUserId AND reciver_id = :userId))")
    fun getDirectMessagesBetweenUsers(userId: String, otherUserId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE _id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
} 