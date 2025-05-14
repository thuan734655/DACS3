package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.HomeApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Dashboard
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val api: HomeApi
) {
    suspend fun getDashboard(): Response<ApiResponse<Dashboard>> {
        return try {
            api.getDashboard()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error getting dashboard", e)
            throw e
        }
    }
    
    suspend fun getRecentActivities(
        page: Int? = null,
        limit: Int? = null
    ): Response<ApiResponse<Any>> {
        return try {
            api.getRecentActivities(page, limit)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error getting recent activities", e)
            throw e
        }
    }
    
    suspend fun getStatistics(): Response<ApiResponse<Any>> {
        return try {
            api.getStatistics()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error getting statistics", e)
            throw e
        }
    }
    
    suspend fun getWorkspaceOverview(workspaceId: String): Response<ApiResponse<Any>> {
        return try {
            api.getWorkspaceOverview(workspaceId)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error getting workspace overview", e)
            throw e
        }
    }
} 