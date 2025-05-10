package com.example.dacs3.di

import android.content.Context
import com.example.dacs3.data.local.*
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
    
    @Singleton
    @Provides
    fun provideWorkspaceDatabase(@ApplicationContext context: Context): WorkspaceDatabase {
        return WorkspaceDatabase.getDatabase(context)
    }
    
    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
    
    @Provides
    fun provideUserDao(database: WorkspaceDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideAccountDao(database: WorkspaceDatabase): AccountDao {
        return database.accountDao()
    }
    
    @Provides
    fun provideWorkspaceDao(database: WorkspaceDatabase): WorkspaceDao {
        return database.workspaceDao()
    }
    
    @Provides
    fun provideChannelDao(database: WorkspaceDatabase): ChannelDao {
        return database.channelDao()
    }
    
    @Provides
    fun provideMessageDao(database: WorkspaceDatabase): MessageDao {
        return database.messageDao()
    }
    
    @Provides
    fun provideTaskDao(database: WorkspaceDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    fun provideBugDao(database: WorkspaceDatabase): BugDao {
        return database.bugDao()
    }
    
    @Provides
    fun provideEpicDao(database: WorkspaceDatabase): EpicDao {
        return database.epicDao()
    }
    
    @Provides
    fun provideNotificationDao(database: WorkspaceDatabase): NotificationDao {
        return database.notificationDao()
    }
    
    @Provides
    fun provideInvitationDao(database: WorkspaceDatabase): InvitationDao {
        return database.invitationDao()
    }
    
    @Provides
    fun provideUserChannelMembershipDao(database: WorkspaceDatabase): UserChannelMembershipDao {
        return database.userChannelMembershipDao()
    }
    
    @Provides
    fun provideWorkspaceUserMembershipDao(database: WorkspaceDatabase): WorkspaceUserMembershipDao {
        return database.workspaceUserMembershipDao()
    }
} 