package com.example.dacs3.data.network

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Workspace
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API interface for workspace operations
 */
interface WorkspaceApi {
    
    /**
     * Get all workspaces for the current user
     * @param authHeader Authorization header with bearer token
     * @return ApiResponse containing list of workspaces
     */
    @GET("api/workspaces")
    suspend fun getWorkspaces(
        @Header("Authorization") authHeader: String
    ): ApiResponse<List<Workspace>>
    
    /**
     * Create a new workspace
     * @param authHeader Authorization header with bearer token
     * @param workspaceData Map containing workspace data (name, description)
     * @return ApiResponse containing created workspace
     */
    @POST("api/workspaces")
    suspend fun createWorkspace(
        @Header("Authorization") authHeader: String,
        @Body workspaceData: Map<String, String>
    ): ApiResponse<Workspace>
    
    /**
     * Get workspace by ID
     * @param authHeader Authorization header with bearer token
     * @param workspaceId ID of the workspace
     * @return ApiResponse containing workspace object
     */
    @GET("api/workspaces/{workspaceId}")
    suspend fun getWorkspaceById(
        @Header("Authorization") authHeader: String,
        @Path("workspaceId") workspaceId: String
    ): ApiResponse<Workspace>
} 