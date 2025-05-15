package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.SprintEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SprintDao {
    @Query("SELECT * FROM sprints")
    fun getAllSprints(): Flow<List<SprintEntity>>

    @Query("SELECT * FROM sprints WHERE _id = :id")
    suspend fun getSprintById(id: String): SprintEntity?

    @Query("SELECT * FROM sprints WHERE workspace_id = :workspaceId")
    fun getSprintsByWorkspaceId(workspaceId: String): Flow<List<SprintEntity>>

    @Query("SELECT * FROM sprints WHERE status = :status")
    fun getSprintsByStatus(status: String): Flow<List<SprintEntity>>

    @Query("SELECT * FROM sprints WHERE created_by = :userId")
    fun getSprintsByUserId(userId: String): Flow<List<SprintEntity>>

    @Query("SELECT * FROM sprints WHERE start_date <= :currentDate AND end_date >= :currentDate")
    fun getActiveSprints(currentDate: Date): Flow<List<SprintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprint(sprint: SprintEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprints(sprints: List<SprintEntity>)

    @Update
    suspend fun updateSprint(sprint: SprintEntity)

    @Delete
    suspend fun deleteSprint(sprint: SprintEntity)

    @Query("DELETE FROM sprints WHERE _id = :id")
    suspend fun deleteSprintById(id: String)

    @Query("DELETE FROM sprints")
    suspend fun deleteAllSprints()
} 