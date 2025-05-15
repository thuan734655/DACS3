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
        return taskDao.getAllTasks()
    }
    
    override suspend fun getById(id: String): TaskEntity? {
        return taskDao.getTaskById(id)
    }
    
    override suspend fun insert(item: TaskEntity) {
        taskDao.insertTask(item)
    }
    
    override suspend fun insertAll(items: List<TaskEntity>) {
        taskDao.insertTasks(items)
    }
    
    override suspend fun update(item: TaskEntity) {
        taskDao.updateTask(item)
    }
    
    override suspend fun delete(item: TaskEntity) {
        taskDao.deleteTask(item)
    }
    
    override suspend fun deleteById(id: String) {
        taskDao.deleteTaskById(id)
    }
    
    override suspend fun deleteAll() {
        taskDao.deleteAllTasks()
    }
    
    override suspend fun sync() {
        try {
            val response = taskApi.getAllTasks()
            if (response.success && response.data != null) {
                val tasks = response.data.map { TaskEntity.fromTask(it) }
                taskDao.insertTasks(tasks)
                Log.d(TAG, "Successfully synced ${tasks.size} tasks")
            } else {
                Log.w(TAG, "Failed to sync tasks")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing tasks", e)
        }
    }
    
    override fun getTasksByWorkspaceId(workspaceId: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByWorkspaceId(workspaceId)
    }
    
    override fun getTasksByEpicId(epicId: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByEpicId(epicId)
    }
    
    override fun getTasksByStatus(status: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByStatus(status)
    }
    
    override fun getTasksByAssignedTo(assignedTo: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByAssignedTo(assignedTo)
    }
    
    override fun getTasksBySprintId(sprintId: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksBySprintId(sprintId)
    }
    
    override fun getTasksByPriority(priority: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByPriority(priority)
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
        return try {
            val response = taskApi.getAllTasks(
                page, limit, workspaceId, epicId, status, assignedTo, sprintId, priority
            )
            
            // If successful, store tasks in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val taskEntities = response.data.map { TaskEntity.fromTask(it) }
                    taskDao.insertTasks(taskEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tasks from API", e)
            // Return empty response with success=false when API fails
            TaskListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getTaskByIdFromApi(id: String): TaskResponse {
        return try {
            val response = taskApi.getTaskById(id)
            
            // If successful, store task in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val taskEntity = TaskEntity.fromTask(response.data)
                    taskDao.insertTask(taskEntity)
                }
            }
            
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
            
            // If successful, store task in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val taskEntity = TaskEntity.fromTask(response.data)
                    taskDao.insertTask(taskEntity)
                }
            }
            
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
            
            // If successful, update task in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val taskEntity = TaskEntity.fromTask(response.data)
                    taskDao.updateTask(taskEntity)
                }
            }
            
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
            
            // If successful, delete task from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    taskDao.deleteTaskById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
            false
        }
    }
    
    override suspend fun addComment(taskId: String, content: String): CommentResponse {
        return try {
            val request = AddCommentRequest(content)
            val response = taskApi.addComment(taskId, request)
            
            // If successful, update task in local database with the new comment
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    // Get the current task
                    val task = taskDao.getTaskById(taskId)
                    
                    // If the task exists locally, update it
                    if (task != null) {
                        val updatedTask = getTaskByIdFromApi(taskId)
                        if (updatedTask.success && updatedTask.data != null) {
                            val taskEntity = TaskEntity.fromTask(updatedTask.data)
                            taskDao.updateTask(taskEntity)
                        }
                    }
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding comment to task", e)
            // Return empty response with success=false when API fails
            CommentResponse(false, null)
        }
    }
} 