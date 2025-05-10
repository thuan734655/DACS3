package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications")
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    suspend fun getNotificationById(notificationId: String): NotificationEntity?
    
    @Query("SELECT * FROM notifications WHERE receiverId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE receiverId = :userId AND status = 'UNREAD'")
    fun getUnreadNotificationsForUser(userId: String): Flow<List<NotificationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
    
    @Update
    suspend fun updateNotification(notification: NotificationEntity)
    
    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)
    
    @Query("UPDATE notifications SET status = 'READ' WHERE notificationId = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)
    
    @Query("UPDATE notifications SET status = 'READ' WHERE receiverId = :userId")
    suspend fun markAllNotificationsAsRead(userId: String)
    
    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationCount(): Int
} 