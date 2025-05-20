package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.TaskApi
import com.example.dacs3.data.local.dao.TaskDao
import com.example.dacs3.data.local.entity.TaskEntity
import com.example.dacs3.data.model.AddCommentRequest
import com.example.dacs3.data.model.CommentResponse
import com.example.dacs3.data.model.CreateTaskRequest
import com.example.dacs3.data.model.TaskListResponse
import com.example.dacs3.data.model.TaskResponse
import com.example.dacs3.data.model.UpdateTaskRequest
import com.example.dacs3.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskApi: TaskApi
) : TaskRepository {
    
    private val TAG = "TaskRepositoryImpl"
    
    override fun getAll(): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override suspend fun getById(id: String): TaskEntity? {
        TODO()
    }
    
    override suspend fun insert(item: TaskEntity) {
        TODO()
    }
    
    override suspend fun insertAll(items: List<TaskEntity>) {
        TODO()
    }
    
    override suspend fun update(item: TaskEntity) {
        TODO()
    }
    
    override suspend fun delete(item: TaskEntity) {
        TODO()
    }
    
    override suspend fun deleteById(id: String) {
        TODO()
    }
    
    override suspend fun deleteAll() {
        TODO()
    }
    
    override suspend fun sync() {
        TODO()
    }
    
    override fun getTasksByWorkspaceId(workspaceId: String): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override fun getTasksByEpicId(epicId: String): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override fun getTasksByStatus(status: String): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override fun getTasksByAssignedTo(assignedTo: String): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override fun getTasksBySprintId(sprintId: String): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override fun getTasksByPriority(priority: String): Flow<List<TaskEntity>> {
        TODO()
    }
    
    override suspend fun getAllTasksFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        epicId: String?,
        status: String?,
        assignedTo: String?,
        sprintId: String?,
        priority: String?
    ): TaskListResponse {
        Log.d(TAG, "getAllTasksFromApi called with workspaceId: $workspaceId, epicId: $epicId, sprintId: $sprintId")
        return try {
            Log.d(TAG, "Making API call to get tasks with params: page=$page, limit=$limit, workspaceId=$workspaceId, epicId=$epicId, status=$status, assignedTo=$assignedTo, sprintId=$sprintId, priority=$priority")
            val response = taskApi.getAllTasks(
                page, limit, workspaceId, epicId, status, assignedTo, sprintId, priority
            )
            Log.d(TAG, "API call successful, received ${response.data.size} tasks, success=${response.success}, total=${response.total}")

            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tasks from API: ${e.message}", e)
            // Return empty response with success=false when API fails
            TaskListResponse(false, 0, 0, emptyList())
        }
    }

    override suspend fun getTaskById(id: String): TaskResponse {
        Log.d(TAG, "getTaskById called with id: $id")
        return try {
            val response = taskApi.getTaskById(id)
            Log.d(TAG, "Task with id $id loaded successfully: ${response.success}")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching task by ID: ${e.message}", e)
            // Return empty response with success=false when API fails
            TaskResponse(false, null)
        }
    }

    suspend fun getTaskByIdFromApi(id: String): TaskResponse {
        return try {
            val response = taskApi.getTaskById(id)

            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching task from API", e)
            // Return empty response with success=false when API fails
            TaskResponse(false, null)
        }
    }
    
    override suspend fun createTask(
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
    ): TaskResponse {
        return try {
            val request = CreateTaskRequest(
                title, description, workspaceId, epicId, assignedTo, status, priority,
                estimatedHours, spentHours, startDate, dueDate, sprintId
            )
            val response = taskApi.createTask(request)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task", e)
            // Return empty response with success=false when API fails
            TaskResponse(false, null)
        }
    }
    
    override suspend fun updateTask(
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
    ): TaskResponse {
        return try {
            val request = UpdateTaskRequest(
                title, description, epicId, assignedTo, status, priority,
                estimatedHours, spentHours, startDate, dueDate, completedDate, sprintId
            )
            val response = taskApi.updateTask(id, request)

            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
            // Return empty response with success=false when API fails
            TaskResponse(false, null)
        }
    }
    
    override suspend fun deleteTaskFromApi(id: String): Boolean {
        return try {
            val response = taskApi.deleteTask(id)

            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
            false
        }
    }
    
    override suspend fun deleteTask(id: String): TaskResponse {
        TODO()
    }
    
    override suspend fun addComment(taskId: String, content: String): CommentResponse {
        return try {
            val request = AddCommentRequest(content)
            val response = taskApi.addComment(taskId, request)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding comment to task", e)
            // Return empty response with success=false when API fails
            CommentResponse(false, null)
        }
    }
} 