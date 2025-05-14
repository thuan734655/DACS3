package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Workspace
import retrofit2.Response
import retrofit2.http.*

interface WorkspaceApi {
    @GET("workspaces")
    suspend fun getAllWorkspaces(): Response<ApiResponse<List<Workspace>>>
    
    @GET("workspaces/{workspaceId}")
    suspend fun getWorkspaceById(@Path("workspaceId") workspaceId: String): Response<ApiResponse<Workspace>>
    
    @POST("workspaces")
    suspend fun createWorkspace(@Body workspace: Workspace): Response<ApiResponse<Workspace>>
    
    @PUT("workspaces/{workspaceId}")
    suspend fun updateWorkspace(
        @Path("workspaceId") workspaceId: String,
        @Body workspace: Workspace
    ): Response<ApiResponse<Workspace>>
    
    @DELETE("workspaces/{workspaceId}")
    suspend fun deleteWorkspace(@Path("workspaceId") workspaceId: String): Response<ApiResponse<Any>>
    
    @POST("workspaces/{workspaceId}/members")
    suspend fun addMember(
        @Path("workspaceId") workspaceId: String,
        @Body memberData: Map<String, String>
    ): Response<ApiResponse<Workspace>>
    
    @DELETE("workspaces/{workspaceId}/members/{memberId}")
    suspend fun removeMember(
        @Path("workspaceId") workspaceId: String,
        @Path("memberId") memberId: String
    ): Response<ApiResponse<Workspace>>
} 