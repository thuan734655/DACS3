package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.BugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BugDao {
    @Query("SELECT * FROM bugs")
    fun getAllBugs(): Flow<List<BugEntity>>

    @Query("SELECT * FROM bugs WHERE _id = :id")
    suspend fun getBugById(id: String): BugEntity?

    @Query("SELECT * FROM bugs WHERE workspace_id = :workspaceId")
    fun getBugsByWorkspaceId(workspaceId: String): Flow<List<BugEntity>>

    @Query("SELECT * FROM bugs WHERE task_id = :taskId")
    fun getBugsByTaskId(taskId: String): Flow<List<BugEntity>>

    @Query("SELECT * FROM bugs WHERE reported_by = :reportedBy")
    fun getBugsByReportedBy(reportedBy: String): Flow<List<BugEntity>>

    @Query("SELECT * FROM bugs WHERE assigned_to = :assignedTo")
    fun getBugsByAssignedTo(assignedTo: String): Flow<List<BugEntity>>

    @Query("SELECT * FROM bugs WHERE status = :status")
    fun getBugsByStatus(status: String): Flow<List<BugEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBug(bug: BugEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBugs(bugs: List<BugEntity>)

    @Update
    suspend fun updateBug(bug: BugEntity)

    @Delete
    suspend fun deleteBug(bug: BugEntity)

    @Query("DELETE FROM bugs WHERE _id = :id")
    suspend fun deleteBugById(id: String)

    @Query("DELETE FROM bugs")
    suspend fun deleteAllBugs()
} 