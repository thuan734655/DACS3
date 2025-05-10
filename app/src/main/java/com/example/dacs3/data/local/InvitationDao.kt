package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvitationDao {
    @Query("SELECT * FROM invitations")
    fun getAllInvitations(): Flow<List<InvitationEntity>>
    
    @Query("SELECT * FROM invitations WHERE invitationId = :invitationId")
    suspend fun getInvitationById(invitationId: String): InvitationEntity?
    
    @Query("SELECT * FROM invitations WHERE receiverId = :userId")
    fun getInvitationsForUser(userId: String): Flow<List<InvitationEntity>>
    
    @Query("SELECT * FROM invitations WHERE workspaceId = :workspaceId")
    fun getInvitationsForWorkspace(workspaceId: String): Flow<List<InvitationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: InvitationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitations(invitations: List<InvitationEntity>)
    
    @Update
    suspend fun updateInvitation(invitation: InvitationEntity)
    
    @Delete
    suspend fun deleteInvitation(invitation: InvitationEntity)
    
    @Query("UPDATE invitations SET status = :status WHERE invitationId = :invitationId")
    suspend fun updateInvitationStatus(invitationId: String, status: InvitationStatus)
    
    @Query("SELECT COUNT(*) FROM invitations")
    suspend fun getInvitationCount(): Int
} 