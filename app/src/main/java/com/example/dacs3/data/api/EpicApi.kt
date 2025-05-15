package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface EpicApi {
    // GET all epics with pagination
    @GET("epics")
    suspend fun getAllEpics(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("status") status: String? = null,
        @Query("assigned_to") assignedTo: String? = null,
        @Query("sprint_id") sprintId: String? = null
    ): EpicListResponse

    // GET epic by ID
    @GET("epics/{id}")
    suspend fun getEpicById(@Path("id") id: String): EpicResponse

    // POST create new epic
    @POST("epics")
    suspend fun createEpic(@Body request: CreateEpicRequest): EpicResponse

    // PUT update epic
    @PUT("epics/{id}")
    suspend fun updateEpic(
        @Path("id") id: String,
        @Body request: UpdateEpicRequest
    ): EpicResponse

    // DELETE epic
    @DELETE("epics/{id}")
    suspend fun deleteEpic(@Path("id") id: String): MessageResponse
}