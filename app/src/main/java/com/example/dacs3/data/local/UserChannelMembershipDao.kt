package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserChannelMembershipDao {
    @Query("SELECT * FROM user_channel_memberships WHERE userId = :userId")
    fun getUserChannelMemberships(userId: String): Flow<List<UserChannelMembership>>
    
    @Query("SELECT * FROM user_channel_memberships WHERE channelId = :channelId")
    fun getChannelMembers(channelId: String): Flow<List<UserChannelMembership>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: UserChannelMembership)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemberships(memberships: List<UserChannelMembership>)
    
    @Query("DELETE FROM user_channel_memberships WHERE userId = :userId AND channelId = :channelId")
    suspend fun removeMembership(userId: String, channelId: String)
    
    @Query("SELECT COUNT(*) FROM user_channel_memberships WHERE channelId = :channelId")
    suspend fun getChannelMemberCount(channelId: String): Int
} 