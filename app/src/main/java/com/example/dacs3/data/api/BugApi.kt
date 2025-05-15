package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface BugApi {
    // GET all bugs with pagination
    @GET("bugs")
    suspend fun getAllBugs(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("task_id") taskId: String? = null,
        @Query("reported_by") reportedBy: String? = null,
        @Query("assigned_to") assignedTo: String? = null,
        @Query("status") status: String? = null,
        @Query("severity") severity: String? = null
    ): BugListResponse

    // GET bug by ID
    @GET("bugs/{id}")
    suspend fun getBugById(@Path("id") id: String): BugResponse

    // POST create new bug
    @POST("bugs")
    suspend fun createBug(@Body request: CreateBugRequest): BugResponse

    // PUT update bug
    @PUT("bugs/{id}")
    suspend fun updateBug(
        @Path("id") id: String,
        @Body request: UpdateBugRequest
    ): BugResponse

    // DELETE bug
    @DELETE("bugs/{id}")
    suspend fun deleteBug(@Path("id") id: String): MessageResponse

    // POST add comment to bug
    @POST("bugs/{id}/comments")
    suspend fun addComment(
        @Path("id") id: String,
        @Body request: AddCommentRequest
    ): CommentResponse
}