package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Notification
import retrofit2.Response
import retrofit2.http.*

interface NotificationApi {
    @GET("notifications")
    suspend fun getAllNotifications(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<List<Notification>>>
    
    @GET("notifications/unread")
    suspend fun getUnreadNotifications(): Response<ApiResponse<List<Notification>>>
    
    @PUT("notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: String): Response<ApiResponse<Notification>>
    
    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): Response<ApiResponse<Any>>
    
    @DELETE("notifications/{notificationId}")
    suspend fun deleteNotification(@Path("notificationId") notificationId: String): Response<ApiResponse<Any>>
    
    @POST("notifications/token")
    suspend fun registerFcmToken(@Body tokenData: Map<String, String>): Response<ApiResponse<Any>>
} 