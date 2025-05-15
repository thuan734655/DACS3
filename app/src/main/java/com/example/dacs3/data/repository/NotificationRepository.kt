package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.NotificationEntity
import com.example.dacs3.data.model.NotificationListResponse
import com.example.dacs3.data.model.NotificationResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface NotificationRepository : BaseRepository<NotificationEntity, String> {
    /**
     * Get notifications by workspace ID from local database
     */
    fun getNotificationsByWorkspaceId(workspaceId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get notifications by user ID from local database
     */
    fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get notifications by type from local database
     */
    fun getNotificationsByType(type: String): Flow<List<NotificationEntity>>
    
    /**
     * Get unread notifications from local database
     */
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>
    
    /**
     * Get all notifications from remote API with pagination and filters
     */
    suspend fun getAllNotificationsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        type: String? = null
    ): NotificationListResponse
    
    /**
     * Get unread notifications from remote API
     */
    suspend fun getUnreadNotificationsFromApi(): NotificationListResponse
    
    /**
     * Get notification by ID from remote API
     */
    suspend fun getNotificationByIdFromApi(id: String): NotificationResponse
    
    /**
     * Create a new notification on the remote API
     */
    suspend fun createNotification(
        userId: String,
        type: String,
        typeId: String?,
        workspaceId: String,
        content: String,
        relatedId: String?
    ): NotificationResponse
    
    /**
     * Mark notification as read on the remote API
     */
    suspend fun markAsRead(id: String): NotificationResponse
    
    /**
     * Mark all notifications as read on the remote API
     */
    suspend fun markAllAsRead(workspaceId: String? = null): Boolean
    
    /**
     * Delete a notification on the remote API
     */
    suspend fun deleteNotificationFromApi(id: String): Boolean
    
    /**
     * Mark notification as read locally
     */
    suspend fun markAsReadLocally(id: String)
    
    /**
     * Mark all notifications as read locally
     */
    suspend fun markAllAsReadLocally(userId: String)
    
    /**
     * Mark all notifications in a workspace as read locally
     */
    suspend fun markAllAsReadByWorkspaceLocally(userId: String, workspaceId: String)
} 