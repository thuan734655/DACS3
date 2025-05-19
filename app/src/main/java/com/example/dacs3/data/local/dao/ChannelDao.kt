package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
//    @Query("SELECT * FROM channels")
//    fun getAllChannels(): Flow<List<ChannelEntity>>
//
//    @Query("SELECT * FROM channels WHERE _id = :id")
//    suspend fun getChannelById(id: String): ChannelEntity?
//
//    @Query("SELECT * FROM channels WHERE workspace_id = :workspaceId")
//    fun getChannelsByWorkspaceId(workspaceId: String): Flow<List<ChannelEntity>>
//
//    @Query("SELECT * FROM channels WHERE created_by = :createdBy")
//    fun getChannelsByCreatedBy(createdBy: String): Flow<List<ChannelEntity>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertChannel(channel: ChannelEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertChannels(channels: List<ChannelEntity>)
//
//    @Update
//    suspend fun updateChannel(channel: ChannelEntity)
//
//    @Delete
//    suspend fun deleteChannel(channel: ChannelEntity)
//
//    @Query("DELETE FROM channels WHERE _id = :id")
//    suspend fun deleteChannelById(id: String)
//
//    @Query("DELETE FROM channels")
//    suspend fun deleteAllChannels()
} 