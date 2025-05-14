package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.TaskApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Task
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val api: TaskApi
) {
    suspend fun getAllTasks(
        workspaceId: String? = null,
        sprintId: String? = null,
        epicId: String? = null,
        assigneeId: String? = null,
        status: String? = null
    ): Response<ApiResponse<List<Task>>> {
        return try {
            api.getAllTasks(workspaceId, sprintId, epicId, assigneeId, status)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error getting all tasks", e)
            throw e
        }
    }
    
    suspend fun getTaskById(taskId: String): Response<ApiResponse<Task>> {
        return try {
            api.getTaskById(taskId)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error getting task by id", e)
            throw e
        }
    }
    
    suspend fun createTask(task: Task): Response<ApiResponse<Task>> {
        return try {
            api.createTask(task)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error creating task", e)
            throw e
        }
    }
    
    suspend fun updateTask(taskId: String, task: Task): Response<ApiResponse<Task>> {
        return try {
            api.updateTask(taskId, task)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task", e)
            throw e
        }
    }
    
    suspend fun deleteTask(taskId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteTask(taskId)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting task", e)
            throw e
        }
    }
    
    suspend fun updateTaskStatus(taskId: String, status: String): Response<ApiResponse<Task>> {
        return try {
            val statusData = mapOf("status" to status)
            api.updateTaskStatus(taskId, statusData)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task status", e)
            throw e
        }
    }
    
    suspend fun updateTaskAssignee(taskId: String, assigneeId: String): Response<ApiResponse<Task>> {
        return try {
            val assigneeData = mapOf("assigneeId" to assigneeId)
            api.updateTaskAssignee(taskId, assigneeData)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task assignee", e)
            throw e
        }
    }
} 