package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.SprintApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Sprint
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SprintRepository @Inject constructor(
    private val api: SprintApi
) {
    suspend fun getAllSprints(
        workspaceId: String? = null,
        status: String? = null
    ): Response<ApiResponse<List<Sprint>>> {
        return try {
            api.getAllSprints(workspaceId, status)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error getting all sprints", e)
            throw e
        }
    }
    
    suspend fun getSprintById(sprintId: String): Response<ApiResponse<Sprint>> {
        return try {
            api.getSprintById(sprintId)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error getting sprint by id", e)
            throw e
        }
    }
    
    suspend fun createSprint(sprint: Sprint): Response<ApiResponse<Sprint>> {
        return try {
            api.createSprint(sprint)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error creating sprint", e)
            throw e
        }
    }
    
    suspend fun updateSprint(sprintId: String, sprint: Sprint): Response<ApiResponse<Sprint>> {
        return try {
            api.updateSprint(sprintId, sprint)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error updating sprint", e)
            throw e
        }
    }
    
    suspend fun deleteSprint(sprintId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteSprint(sprintId)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error deleting sprint", e)
            throw e
        }
    }
    
    suspend fun updateSprintStatus(sprintId: String, status: String): Response<ApiResponse<Sprint>> {
        return try {
            val statusData = mapOf("status" to status)
            api.updateSprintStatus(sprintId, statusData)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error updating sprint status", e)
            throw e
        }
    }
    
    suspend fun getSprintTasks(sprintId: String): Response<ApiResponse<Any>> {
        return try {
            api.getSprintTasks(sprintId)
        } catch (e: Exception) {
            Log.e("SprintRepository", "Error getting sprint tasks", e)
            throw e
        }
    }
} 