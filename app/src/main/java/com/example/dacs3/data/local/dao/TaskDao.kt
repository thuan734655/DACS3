package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE _id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE workspace_id = :workspaceId")
    fun getTasksByWorkspaceId(workspaceId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE epic_id = :epicId")
    fun getTasksByEpicId(epicId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE assigned_to = :assignedTo")
    fun getTasksByAssignedTo(assignedTo: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE sprint_id = :sprintId")
    fun getTasksBySprintId(sprintId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE _id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
} 