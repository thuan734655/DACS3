package com.example.dacs3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        WorkspaceEntity::class,
        ChannelEntity::class,
        MessageEntity::class,
        TaskEntity::class,
        BugEntity::class,
        EpicEntity::class,
        NotificationEntity::class,
        InvitationEntity::class,
        UserChannelMembership::class,
        WorkspaceUserMembership::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun channelDao(): ChannelDao
    abstract fun messageDao(): MessageDao
    abstract fun taskDao(): TaskDao
    abstract fun bugDao(): BugDao
    abstract fun epicDao(): EpicDao
    abstract fun notificationDao(): NotificationDao
    abstract fun invitationDao(): InvitationDao
    abstract fun userChannelMembershipDao(): UserChannelMembershipDao
    abstract fun workspaceUserMembershipDao(): WorkspaceUserMembershipDao
    
    companion object {
        @Volatile
        private var INSTANCE: WorkspaceDatabase? = null
        
        fun getDatabase(context: Context): WorkspaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkspaceDatabase::class.java,
                    "workspace_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 