package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Epic
import retrofit2.Response
import retrofit2.http.*

interface EpicApi {
    @GET("epics")
    suspend fun getAllEpics(
        @Query("workspaceId") workspaceId: String? = null
    ): Response<ApiResponse<List<Epic>>>
    
    @GET("epics/{epicId}")
    suspend fun getEpicById(@Path("epicId") epicId: String): Response<ApiResponse<Epic>>
    
    @POST("epics")
    suspend fun createEpic(@Body epic: Epic): Response<ApiResponse<Epic>>
    
    @PUT("epics/{epicId}")
    suspend fun updateEpic(
        @Path("epicId") epicId: String,
        @Body epic: Epic
    ): Response<ApiResponse<Epic>>
    
    @DELETE("epics/{epicId}")
    suspend fun deleteEpic(@Path("epicId") epicId: String): Response<ApiResponse<Any>>
    
    @GET("epics/{epicId}/tasks")
    suspend fun getEpicTasks(@Path("epicId") epicId: String): Response<ApiResponse<Any>>
} 