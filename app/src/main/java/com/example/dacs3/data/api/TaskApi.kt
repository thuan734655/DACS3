package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface TaskApi {
    // GET all tasks with pagination
    @GET("tasks")
    suspend fun getAllTasks(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("epic_id") epicId: String? = null,
        @Query("status") status: String? = null,
        @Query("assigned_to") assignedTo: String? = null,
        @Query("sprint_id") sprintId: String? = null,
        @Query("priority") priority: String? = null
    ): TaskListResponse

    // GET task by ID
    @GET("tasks/{id}")
    suspend fun getTaskById(
        @Path("id") id: String,
        @Query("strictPopulate") strictPopulate: Boolean = false
    ): TaskResponse

    // POST create new task
    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): TaskResponse

    // PUT update task
    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body request: UpdateTaskRequest
    ): TaskResponse

    // DELETE task
    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): MessageResponse

    // POST add comment to task
    @POST("tasks/{id}/comments")
    suspend fun addComment(
        @Path("id") id: String,
        @Body request: AddCommentRequest
    ): CommentResponse
}