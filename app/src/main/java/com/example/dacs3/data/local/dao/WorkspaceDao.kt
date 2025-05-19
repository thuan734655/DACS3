package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.WorkspaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
//    @Query("SELECT * FROM workspaces")
//    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>
//
//    @Query("SELECT * FROM workspaces WHERE _id = :id")
//    suspend fun getWorkspaceById(id: String): WorkspaceEntity?
//
//    @Query("SELECT * FROM workspaces WHERE createdById = :userId")
//    fun getWorkspacesByUserId(userId: String): Flow<List<WorkspaceEntity>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertWorkspace(workspace: WorkspaceEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertWorkspaces(workspaces: List<WorkspaceEntity>)
//
//    @Update
//    suspend fun updateWorkspace(workspace: WorkspaceEntity)
//
//    @Delete
//    suspend fun deleteWorkspace(workspace: WorkspaceEntity)
//
//    @Query("DELETE FROM workspaces WHERE _id = :id")
//    suspend fun deleteWorkspaceById(id: String)
//
//    @Query("DELETE FROM workspaces")
//    suspend fun deleteAllWorkspaces()
} 