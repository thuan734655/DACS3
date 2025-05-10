package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EpicDao {
    @Query("SELECT * FROM epics")
    fun getAllEpics(): Flow<List<EpicEntity>>
    
    @Query("SELECT * FROM epics WHERE epicId = :epicId")
    suspend fun getEpicById(epicId: String): EpicEntity?
    
    @Query("SELECT * FROM epics WHERE workspaceId = :workspaceId")
    fun getEpicsByWorkspace(workspaceId: String): Flow<List<EpicEntity>>
    
    @Query("SELECT * FROM epics WHERE createdBy = :userId")
    fun getEpicsCreatedByUser(userId: String): Flow<List<EpicEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpic(epic: EpicEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpics(epics: List<EpicEntity>)
    
    @Update
    suspend fun updateEpic(epic: EpicEntity)
    
    @Delete
    suspend fun deleteEpic(epic: EpicEntity)
    
    @Query("SELECT COUNT(*) FROM epics")
    suspend fun getEpicCount(): Int
} 