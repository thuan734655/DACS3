package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.EpicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpicDao {
    @Query("SELECT * FROM epics")
    fun getAllEpics(): Flow<List<EpicEntity>>

    @Query("SELECT * FROM epics WHERE _id = :id")
    suspend fun getEpicById(id: String): EpicEntity?

    @Query("SELECT * FROM epics WHERE workspace_id = :workspaceId")
    fun getEpicsByWorkspaceId(workspaceId: String): Flow<List<EpicEntity>>

    @Query("SELECT * FROM epics WHERE status = :status")
    fun getEpicsByStatus(status: String): Flow<List<EpicEntity>>

    @Query("SELECT * FROM epics WHERE assigned_to = :assignedTo")
    fun getEpicsByAssignedTo(assignedTo: String): Flow<List<EpicEntity>>

    @Query("SELECT * FROM epics WHERE sprint_id = :sprintId")
    fun getEpicsBySprintId(sprintId: String): Flow<List<EpicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpic(epic: EpicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpics(epics: List<EpicEntity>)

    @Update
    suspend fun updateEpic(epic: EpicEntity)

    @Delete
    suspend fun deleteEpic(epic: EpicEntity)

    @Query("DELETE FROM epics WHERE _id = :id")
    suspend fun deleteEpicById(id: String)

    @Query("DELETE FROM epics")
    suspend fun deleteAllEpics()
} 