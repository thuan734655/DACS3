package com.example.dacs3.di

import com.example.dacs3.data.repository.FirebaseMessageRepository
import com.example.dacs3.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseMessageRepository(
        sessionManager: SessionManager
    ): FirebaseMessageRepository {
        return FirebaseMessageRepository(sessionManager)
    }
}
