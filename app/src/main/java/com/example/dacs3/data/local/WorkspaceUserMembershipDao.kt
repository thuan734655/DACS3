package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceUserMembershipDao {
    @Query("SELECT * FROM workspace_user_memberships")
    fun getAllMemberships(): Flow<List<WorkspaceUserMembership>>
    
    @Query("SELECT * FROM workspace_user_memberships WHERE userId = :userId")
    fun getUserWorkspaceMemberships(userId: String): Flow<List<WorkspaceUserMembership>>
    
    @Query("SELECT * FROM workspace_user_memberships WHERE workspaceId = :workspaceId")
    fun getWorkspaceMembers(workspaceId: String): Flow<List<WorkspaceUserMembership>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: WorkspaceUserMembership)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemberships(memberships: List<WorkspaceUserMembership>)
    
    @Delete
    suspend fun deleteMembership(membership: WorkspaceUserMembership)
    
    @Query("DELETE FROM workspace_user_memberships WHERE userId = :userId AND workspaceId = :workspaceId")
    suspend fun deleteMembership(userId: String, workspaceId: String)
    
    @Query("SELECT COUNT(*) FROM workspace_user_memberships")
    suspend fun getMembershipCount(): Int
} 