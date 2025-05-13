package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.model.Workspace
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HomeApi {
    // Lấy thông tin workspace
    @GET("api/workspaces/{workspaceId}")
    suspend fun getWorkspace(
        @Header("Authorization") token: String,
        @Path("workspaceId") workspaceId: String
    ): Response<ApiResponse<Workspace>>

    // Lấy danh sách workspace
    @GET("api/workspaces")
    suspend fun getWorkspaces(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Workspace>>>
    
    // Lấy danh sách channel trong workspace
    @GET("api/channel/workspace/{workspaceId}")
    suspend fun getChannels(
        @Header("Authorization") token: String,
        @Path("workspaceId") workspaceId: String
    ): Response<ApiResponse<List<Channel>>>
    
    // Lấy danh sách notification
    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Notification>>>
    
    // Lấy danh sách notification theo workspace
    @GET("api/notifications/workspace/{workspaceId}")
    suspend fun getNotificationsByWorkspace(
        @Header("Authorization") token: String,
        @Path("workspaceId") workspaceId: String
    ): Response<ApiResponse<List<Notification>>>
}

// Cấu trúc response API chung
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val count: Int? = null
) 