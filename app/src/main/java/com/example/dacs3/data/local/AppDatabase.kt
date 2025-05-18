package com.example.dacs3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dacs3.data.local.converters.Converters
import com.example.dacs3.data.local.dao.*
import com.example.dacs3.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        WorkspaceEntity::class,
        BugEntity::class,
        ChannelEntity::class,
        EpicEntity::class,
        NotificationEntity::class,
        ReportDailyEntity::class,
        SprintEntity::class,
        TaskEntity::class,
        AccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // DAOs
    abstract fun userDao(): UserDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun bugDao(): BugDao
    abstract fun channelDao(): ChannelDao
    abstract fun epicDao(): EpicDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reportDailyDao(): ReportDailyDao
    abstract fun sprintDao(): SprintDao
    abstract fun taskDao(): TaskDao
    abstract fun accountDao(): AccountDao

    companion object {
        const val DATABASE_NAME = "dacs3_database"
    }
} 