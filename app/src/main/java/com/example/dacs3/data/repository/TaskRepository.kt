package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.TaskEntity
import com.example.dacs3.data.model.CommentResponse
import com.example.dacs3.data.model.TaskListResponse
import com.example.dacs3.data.model.TaskResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskRepository : BaseRepository<TaskEntity, String> {
    /**
     * Get tasks by workspace ID from local database
     */
    fun getTasksByWorkspaceId(workspaceId: String): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by epic ID from local database
     */
    fun getTasksByEpicId(epicId: String): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by status from local database
     */
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by assigned to from local database
     */
    fun getTasksByAssignedTo(assignedTo: String): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by sprint ID from local database
     */
    fun getTasksBySprintId(sprintId: String): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by priority from local database
     */
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>
    
    /**
     * Get all tasks from remote API with pagination and filters
     */
    suspend fun getAllTasksFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        epicId: String? = null,
        status: String? = null,
        assignedTo: String? = null,
        sprintId: String? = null,
        priority: String? = null
    ): TaskListResponse
    
    /**
     * Get task by ID from remote API
     */
    suspend fun getTaskByIdFromApi(id: String): TaskResponse
    
    /**
     * Create a new task on the remote API
     */
    suspend fun createTask(
        title: String,
        description: String?,
        workspaceId: String,
        epicId: String?,
        assignedTo: String?,
        status: String?,
        priority: String?,
        estimatedHours: Number?,
        spentHours: Number?,
        startDate: Date?,
        dueDate: Date?,
        sprintId: String?
    ): TaskResponse
    
    /**
     * Update a task on the remote API
     */
    suspend fun updateTask(
        id: String,
        title: String?,
        description: String?,
        epicId: String?,
        assignedTo: String?,
        status: String?,
        priority: String?,
        estimatedHours: Number?,
        spentHours: Number?,
        startDate: Date?,
        dueDate: Date?,
        completedDate: Date?,
        sprintId: String?
    ): TaskResponse
    
    /**
     * Delete a task on the remote API
     */
    suspend fun deleteTaskFromApi(id: String): Boolean
    
    /**
     * Delete a task on the remote API and update the UI with result
     */
    suspend fun deleteTask(id: String): TaskResponse
    
    /**
     * Add a comment to a task
     */
    suspend fun addComment(taskId: String, content: String): CommentResponse
} 