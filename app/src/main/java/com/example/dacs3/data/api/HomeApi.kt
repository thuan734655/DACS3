package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Dashboard
import retrofit2.Response
import retrofit2.http.*

interface HomeApi {
    @GET("home/dashboard")
    suspend fun getDashboard(): Response<ApiResponse<Dashboard>>
    
    @GET("home/recent-activities")
    suspend fun getRecentActivities(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<Any>>
    
    @GET("home/statistics")
    suspend fun getStatistics(): Response<ApiResponse<Any>>
    
    @GET("home/workspace/{workspaceId}")
    suspend fun getWorkspaceOverview(@Path("workspaceId") workspaceId: String): Response<ApiResponse<Any>>
} 