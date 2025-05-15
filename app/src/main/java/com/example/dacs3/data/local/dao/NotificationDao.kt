package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE _id = :id")
    suspend fun getNotificationById(id: String): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE workspace_id = :workspaceId")
    fun getNotificationsByWorkspaceId(workspaceId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE user_id = :userId")
    fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE type = :type")
    fun getNotificationsByType(type: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE is_read = 0")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET is_read = 1 WHERE _id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId AND workspace_id = :workspaceId")
    suspend fun markAllAsReadByWorkspace(userId: String, workspaceId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE _id = :id")
    suspend fun deleteNotificationById(id: String)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()
} 