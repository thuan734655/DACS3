package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.NotificationApi
import com.example.dacs3.data.local.dao.NotificationDao
import com.example.dacs3.data.local.entity.NotificationEntity
import com.example.dacs3.data.model.CreateNotificationRequest
import com.example.dacs3.data.model.MarkAllAsReadRequest
import com.example.dacs3.data.model.NotificationListResponse
import com.example.dacs3.data.model.NotificationResponse
import com.example.dacs3.data.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val notificationApi: NotificationApi
) : NotificationRepository {
    
    private val TAG = "NotificationRepositoryImpl"
    
    override fun getAll(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }
    
    override suspend fun getById(id: String): NotificationEntity? {
        return notificationDao.getNotificationById(id)
    }
    
    override suspend fun insert(item: NotificationEntity) {
        notificationDao.insertNotification(item)
    }
    
    override suspend fun insertAll(items: List<NotificationEntity>) {
        notificationDao.insertNotifications(items)
    }
    
    override suspend fun update(item: NotificationEntity) {
        notificationDao.updateNotification(item)
    }
    
    override suspend fun delete(item: NotificationEntity) {
        notificationDao.deleteNotification(item)
    }
    
    override suspend fun deleteById(id: String) {
        notificationDao.deleteNotificationById(id)
    }
    
    override suspend fun deleteAll() {
        notificationDao.deleteAllNotifications()
    }
    
    override suspend fun sync() {
        try {
            val response = notificationApi.getAllNotifications()
            if (response.success && response.data != null) {
                val notifications = response.data.map { NotificationEntity.fromNotification(it) }
                notificationDao.insertNotifications(notifications)
                Log.d(TAG, "Successfully synced ${notifications.size} notifications")
            } else {
                Log.w(TAG, "Failed to sync notifications")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing notifications", e)
        }
    }
    
    override fun getNotificationsByWorkspaceId(workspaceId: String): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsByWorkspaceId(workspaceId)
    }
    
    override fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsByUserId(userId)
    }
    
    override fun getNotificationsByType(type: String): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsByType(type)
    }
    
    override fun getUnreadNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getUnreadNotifications()
    }
    
    override suspend fun getAllNotificationsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        type: String?
    ): NotificationListResponse {
        return try {
            val response = notificationApi.getAllNotifications(page, limit, workspaceId, type)
            
            // If successful, store notifications in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val notificationEntities = response.data.map { NotificationEntity.fromNotification(it) }
                    notificationDao.insertNotifications(notificationEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching notifications from API", e)
            // Return empty response with success=false when API fails
            NotificationListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getUnreadNotificationsFromApi(): NotificationListResponse {
        return try {
            val response = notificationApi.getUnreadNotifications()
            
            // If successful, store notifications in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val notificationEntities = response.data.map { NotificationEntity.fromNotification(it) }
                    notificationDao.insertNotifications(notificationEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching unread notifications from API", e)
            // Return empty response with success=false when API fails
            NotificationListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getNotificationByIdFromApi(id: String): NotificationResponse {
        return try {
            val response = notificationApi.getNotificationById(id)
            
            // If successful, store notification in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val notificationEntity = NotificationEntity.fromNotification(response.data)
                    notificationDao.insertNotification(notificationEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching notification from API", e)
            // Return empty response with success=false when API fails
            NotificationResponse(false, null)
        }
    }
    
    override suspend fun createNotification(
        userId: String,
        type: String,
        typeId: String?,
        workspaceId: String,
        content: String,
        relatedId: String?
    ): NotificationResponse {
        return try {
            val request = CreateNotificationRequest(userId, type, typeId, workspaceId, content, relatedId)
            val response = notificationApi.createNotification(request)
            
            // If successful, store notification in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val notificationEntity = NotificationEntity.fromNotification(response.data)
                    notificationDao.insertNotification(notificationEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification", e)
            // Return empty response with success=false when API fails
            NotificationResponse(false, null)
        }
    }
    
    override suspend fun markAsRead(id: String): NotificationResponse {
        return try {
            val response = notificationApi.markAsRead(id)
            
            // If successful, mark notification as read in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    markAsReadLocally(id)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read", e)
            // Return empty response with success=false when API fails
            NotificationResponse(false, null)
        }
    }
    
    override suspend fun markAllAsRead(workspaceId: String?): Boolean {
        return try {
            val request = MarkAllAsReadRequest(workspaceId)
            val response = notificationApi.markAllAsRead(request)
            
            // If successful, mark all notifications as read in local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    // This is a simplification. In a real implementation, we should have the current user's ID
                    // and mark all notifications for that user as read.
                    val currentUserId = getCurrentUserId()
                    if (workspaceId != null) {
                        markAllAsReadByWorkspaceLocally(currentUserId, workspaceId)
                    } else {
                        markAllAsReadLocally(currentUserId)
                    }
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error marking all notifications as read", e)
            false
        }
    }
    
    override suspend fun deleteNotificationFromApi(id: String): Boolean {
        return try {
            val response = notificationApi.deleteNotification(id)
            
            // If successful, delete notification from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    notificationDao.deleteNotificationById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting notification", e)
            false
        }
    }
    
    override suspend fun markAsReadLocally(id: String) {
        notificationDao.markAsRead(id)
    }
    
    override suspend fun markAllAsReadLocally(userId: String) {
        notificationDao.markAllAsRead(userId)
    }
    
    override suspend fun markAllAsReadByWorkspaceLocally(userId: String, workspaceId: String) {
        notificationDao.markAllAsReadByWorkspace(userId, workspaceId)
    }
    
    // Helper function to get the current user ID
    // In a real implementation, this would be obtained from a user session manager
    private fun getCurrentUserId(): String {
        // This is a placeholder. In a real implementation, get the user ID from the session.
        return ""
    }
} 