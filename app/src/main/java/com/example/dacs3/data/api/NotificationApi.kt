package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface NotificationApi {
    // GET all notifications with pagination and filtering
    @GET("notifications")
    suspend fun getAllNotifications(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("type") type: String? = null
    ): NotificationListResponse

    // GET notifications by user ID with pagination and filtering
    @GET("notifications/user/{userId}")
    suspend fun getNotificationsByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("type") type: String? = null
    ): NotificationListResponse

    // GET unread notifications
    @GET("notifications/unread")
    suspend fun getUnreadNotifications(): NotificationListResponse

    // GET notification by ID
    @GET("notifications/{id}")
    suspend fun getNotificationById(@Path("id") id: String): NotificationResponse

    // POST create new notification
    @POST("notifications")
    suspend fun createNotification(@Body request: CreateNotificationRequest): NotificationResponse

    // PUT mark notification as read
    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): NotificationResponse

    // PUT mark all notifications as read
    @PUT("notifications/read-all")
    suspend fun markAllAsRead(@Body request: MarkAllAsReadRequest): MessageResponse

    // DELETE notification
    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: String): MessageResponse
}