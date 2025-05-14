package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Sprint
import retrofit2.Response
import retrofit2.http.*

interface SprintApi {
    @GET("sprints")
    suspend fun getAllSprints(
        @Query("workspaceId") workspaceId: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Sprint>>>
    
    @GET("sprints/{sprintId}")
    suspend fun getSprintById(@Path("sprintId") sprintId: String): Response<ApiResponse<Sprint>>
    
    @POST("sprints")
    suspend fun createSprint(@Body sprint: Sprint): Response<ApiResponse<Sprint>>
    
    @PUT("sprints/{sprintId}")
    suspend fun updateSprint(
        @Path("sprintId") sprintId: String,
        @Body sprint: Sprint
    ): Response<ApiResponse<Sprint>>
    
    @DELETE("sprints/{sprintId}")
    suspend fun deleteSprint(@Path("sprintId") sprintId: String): Response<ApiResponse<Any>>
    
    @PUT("sprints/{sprintId}/status")
    suspend fun updateSprintStatus(
        @Path("sprintId") sprintId: String,
        @Body statusData: Map<String, String>
    ): Response<ApiResponse<Sprint>>
    
    @GET("sprints/{sprintId}/tasks")
    suspend fun getSprintTasks(@Path("sprintId") sprintId: String): Response<ApiResponse<Any>>
} 