package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces")
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>
    
    @Query("SELECT * FROM workspaces WHERE workspaceId = :workspaceId")
    suspend fun getWorkspaceById(workspaceId: String): WorkspaceEntity?
    
    @Query("SELECT * FROM workspaces WHERE leaderId = :userId OR createdBy = :userId")
    fun getWorkspacesByLeaderOrCreator(userId: String): Flow<List<WorkspaceEntity>>
    
    @Query("SELECT w.* FROM workspaces w JOIN workspace_user_memberships m ON w.workspaceId = m.workspaceId WHERE m.userId = :userId")
    fun getWorkspacesByMember(userId: String): Flow<List<WorkspaceEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaces(workspaces: List<WorkspaceEntity>)
    
    @Update
    suspend fun updateWorkspace(workspace: WorkspaceEntity)
    
    @Delete
    suspend fun deleteWorkspace(workspace: WorkspaceEntity)
    
    @Query("SELECT COUNT(*) FROM workspaces")
    suspend fun getWorkspaceCount(): Int
} 