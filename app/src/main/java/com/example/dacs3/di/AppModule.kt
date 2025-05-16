package com.example.dacs3.di

import android.app.NotificationManager
import android.content.Context
import com.example.dacs3.data.user.UserManager
import com.example.dacs3.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    @Provides
    @Singleton
    fun provideUserManager(sessionManager: SessionManager, userRepository: com.example.dacs3.data.repository.UserRepository): UserManager {
        return UserManager(sessionManager, userRepository)
    }
} 