package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.EpicApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Epic
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpicRepository @Inject constructor(
    private val api: EpicApi
) {
    suspend fun getAllEpics(workspaceId: String? = null): Response<ApiResponse<List<Epic>>> {
        return try {
            api.getAllEpics(workspaceId)
        } catch (e: Exception) {
            Log.e("EpicRepository", "Error getting all epics", e)
            throw e
        }
    }
    
    suspend fun getEpicById(epicId: String): Response<ApiResponse<Epic>> {
        return try {
            api.getEpicById(epicId)
        } catch (e: Exception) {
            Log.e("EpicRepository", "Error getting epic by id", e)
            throw e
        }
    }
    
    suspend fun createEpic(epic: Epic): Response<ApiResponse<Epic>> {
        return try {
            api.createEpic(epic)
        } catch (e: Exception) {
            Log.e("EpicRepository", "Error creating epic", e)
            throw e
        }
    }
    
    suspend fun updateEpic(epicId: String, epic: Epic): Response<ApiResponse<Epic>> {
        return try {
            api.updateEpic(epicId, epic)
        } catch (e: Exception) {
            Log.e("EpicRepository", "Error updating epic", e)
            throw e
        }
    }
    
    suspend fun deleteEpic(epicId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteEpic(epicId)
        } catch (e: Exception) {
            Log.e("EpicRepository", "Error deleting epic", e)
            throw e
        }
    }
    
    suspend fun getEpicTasks(epicId: String): Response<ApiResponse<Any>> {
        return try {
            api.getEpicTasks(epicId)
        } catch (e: Exception) {
            Log.e("EpicRepository", "Error getting epic tasks", e)
            throw e
        }
    }
} 