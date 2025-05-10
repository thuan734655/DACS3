package com.example.dacs3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY timestamp DESC")
    fun getChannelMessages(channelId: String): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY timestamp ASC")
    fun getDirectMessages(userId1: String, userId2: String): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadMessages(userId: String): Flow<List<MessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
    
    @Query("UPDATE messages SET isRead = 1 WHERE messageId = :messageId")
    suspend fun markMessageAsRead(messageId: String)
    
    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :userId")
    suspend fun markAllMessagesAsRead(userId: String)
    
    @Query("SELECT COUNT(*) FROM messages WHERE channelId = :channelId")
    suspend fun getMessageCountByChannelId(channelId: String): Int
    
    @Query("SELECT COUNT(*) FROM messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1)")
    suspend fun getDirectMessageCount(userId1: String, userId2: String): Int
} 