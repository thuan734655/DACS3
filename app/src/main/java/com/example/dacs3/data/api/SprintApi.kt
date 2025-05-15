package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface SprintApi {
    // GET all sprints with pagination
    @GET("sprints")
    suspend fun getAllSprints(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("status") status: String? = null
    ): SprintListResponse

    // GET sprints for the current user
    @GET("sprints/user/me")
    suspend fun getSprintByIdUser(): SprintListResponse

    // GET sprint by ID
    @GET("sprints/{id}")
    suspend fun getSprintById(@Path("id") id: String): SprintResponse

    // POST create new sprint
    @POST("sprints")
    suspend fun createSprint(@Body request: CreateSprintRequest): SprintResponse

    // PUT update sprint
    @PUT("sprints/{id}")
    suspend fun updateSprint(
        @Path("id") id: String,
        @Body request: UpdateSprintRequest
    ): SprintResponse

    // DELETE sprint
    @DELETE("sprints/{id}")
    suspend fun deleteSprint(@Path("id") id: String): MessageResponse

    // PUT add items (tasks) to sprint
    @PUT("sprints/{id}/items")
    suspend fun addItems(
        @Path("id") id: String,
        @Body request: AddItemsRequest
    ): SprintResponse

    // DELETE remove items from sprint
    @DELETE("sprints/{id}/items")
    suspend fun removeItems(
        @Path("id") id: String,
        @Body request: RemoveItemsRequest
    ): SprintResponse
}