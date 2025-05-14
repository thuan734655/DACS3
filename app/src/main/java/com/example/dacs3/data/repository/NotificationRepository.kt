package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.NotificationApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Notification
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val api: NotificationApi
) {
    suspend fun getAllNotifications(
        page: Int? = null,
        limit: Int? = null
    ): Response<ApiResponse<List<Notification>>> {
        return try {
            api.getAllNotifications(page, limit)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error getting all notifications", e)
            throw e
        }
    }
    
    suspend fun getUnreadNotifications(): Response<ApiResponse<List<Notification>>> {
        return try {
            api.getUnreadNotifications()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error getting unread notifications", e)
            throw e
        }
    }
    
    suspend fun markAsRead(notificationId: String): Response<ApiResponse<Notification>> {
        return try {
            api.markAsRead(notificationId)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error marking notification as read", e)
            throw e
        }
    }
    
    suspend fun markAllAsRead(): Response<ApiResponse<Any>> {
        return try {
            api.markAllAsRead()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error marking all notifications as read", e)
            throw e
        }
    }
    
    suspend fun deleteNotification(notificationId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteNotification(notificationId)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error deleting notification", e)
            throw e
        }
    }
    
    suspend fun registerFcmToken(token: String): Response<ApiResponse<Any>> {
        return try {
            val tokenData = mapOf("token" to token)
            api.registerFcmToken(tokenData)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error registering FCM token", e)
            throw e
        }
    }
} 