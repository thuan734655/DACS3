package com.example.dacs3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels")
    fun getAllChannels(): Flow<List<ChannelEntity>>
    
    @Query("SELECT * FROM channels WHERE channelId = :channelId")
    suspend fun getChannelById(channelId: String): ChannelEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)
    
    @Query("DELETE FROM channels WHERE channelId = :channelId")
    suspend fun deleteChannel(channelId: String)
    
    @Query("SELECT * FROM channels WHERE workspaceId = :workspaceId")
    fun getChannelsByWorkspaceId(workspaceId: String): Flow<List<ChannelEntity>>
    
    @Query("SELECT COUNT(*) FROM channels WHERE workspaceId = :workspaceId")
    suspend fun getChannelCountByWorkspaceId(workspaceId: String): Int
    
    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelCount(): Int
} 