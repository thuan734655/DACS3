package com.example.dacs3.di

import android.content.Context
import androidx.room.Room
import com.example.dacs3.data.local.AppDatabase
import com.example.dacs3.data.local.dao.*
import com.example.dacs3.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
    
    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
    
    @Provides
    @Singleton
    fun provideWorkspaceDao(appDatabase: AppDatabase): WorkspaceDao {
        return appDatabase.workspaceDao()
    }
    
    @Provides
    @Singleton
    fun provideBugDao(appDatabase: AppDatabase): BugDao {
        return appDatabase.bugDao()
    }
    
    @Provides
    @Singleton
    fun provideChannelDao(appDatabase: AppDatabase): ChannelDao {
        return appDatabase.channelDao()
    }
    
    @Provides
    @Singleton
    fun provideEpicDao(appDatabase: AppDatabase): EpicDao {
        return appDatabase.epicDao()
    }
    
    @Provides
    @Singleton
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.messageDao()
    }
    
    @Provides
    @Singleton
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }
    
    @Provides
    @Singleton
    fun provideReportDailyDao(appDatabase: AppDatabase): ReportDailyDao {
        return appDatabase.reportDailyDao()
    }
    
    @Provides
    @Singleton
    fun provideSprintDao(appDatabase: AppDatabase): SprintDao {
        return appDatabase.sprintDao()
    }
    
    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }
    
    @Provides
    @Singleton
    fun provideAccountDao(appDatabase: AppDatabase): AccountDao {
        return appDatabase.accountDao()
    }
} 