package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface WorkspaceApi {
    // GET all workspaces with pagination
    @GET("workspaces")
    suspend fun getAllWorkspaces(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): WorkspaceListResponse

    // GET all workspaces by user ID
    @GET("workspaces/user/{userId}")
    suspend fun getAllWorkspacesByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): WorkspaceListResponse

    // GET workspace by ID
    @GET("workspaces/{id}")
    suspend fun getWorkspaceById(@Path("id") id: String): WorkspaceResponse

    // POST create a new workspace
    @POST("workspaces")
    suspend fun createWorkspace(@Body request: CreateWorkspaceRequest): WorkspaceResponse

    // PUT update a workspace
    @PUT("workspaces/{id}")
    suspend fun updateWorkspace(
        @Path("id") id: String,
        @Body request: UpdateWorkspaceRequest
    ): WorkspaceResponse

    // DELETE a workspace
    @DELETE("workspaces/{id}")
    suspend fun deleteWorkspace(@Path("id") id: String): MessageResponse

    // PUT add member to workspace
    @PUT("workspaces/{id}/members")
    suspend fun addMember(
        @Path("id") id: String,
        @Body request: AddMemberRequest
    ): WorkspaceResponse

    // DELETE remove member from workspace
    @DELETE("workspaces/{id}/members/{userId}")
    suspend fun removeMember(
        @Path("id") id: String,
        @Path("userId") userId: String
    ): WorkspaceResponse

    // POST join a workspace
    @POST("workspaces/{id}/join")
    suspend fun joinWorkspace(@Path("id") id: String): WorkspaceResponse

    // DELETE leave a workspace
    @DELETE("workspaces/{id}/leave")
    suspend fun leaveWorkspace(@Path("id") id: String): MessageResponse

    // GET workspace members with user details
    @GET("workspaces/{id}/members")
    suspend fun getWorkspaceMembers(@Path("id") id: String): UserListResponse
}