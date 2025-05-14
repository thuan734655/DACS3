package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Task
import retrofit2.Response
import retrofit2.http.*

interface TaskApi {
    @GET("tasks")
    suspend fun getAllTasks(
        @Query("workspaceId") workspaceId: String? = null,
        @Query("sprintId") sprintId: String? = null,
        @Query("epicId") epicId: String? = null,
        @Query("assigneeId") assigneeId: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Task>>>
    
    @GET("tasks/{taskId}")
    suspend fun getTaskById(@Path("taskId") taskId: String): Response<ApiResponse<Task>>
    
    @POST("tasks")
    suspend fun createTask(@Body task: Task): Response<ApiResponse<Task>>
    
    @PUT("tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: String,
        @Body task: Task
    ): Response<ApiResponse<Task>>
    
    @DELETE("tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: String): Response<ApiResponse<Any>>
    
    @PUT("tasks/{taskId}/status")
    suspend fun updateTaskStatus(
        @Path("taskId") taskId: String,
        @Body statusData: Map<String, String>
    ): Response<ApiResponse<Task>>
    
    @PUT("tasks/{taskId}/assignee")
    suspend fun updateTaskAssignee(
        @Path("taskId") taskId: String,
        @Body assigneeData: Map<String, String>
    ): Response<ApiResponse<Task>>
} 