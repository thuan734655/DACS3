package com.example.dacs3.di

import com.example.dacs3.data.network.WorkspaceApi
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.repository.WorkspaceRepositoryImpl
import com.example.dacs3.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideWorkspaceRepository(
        workspaceApi: WorkspaceApi,
        sessionManager: SessionManager
    ): WorkspaceRepository {
        return WorkspaceRepositoryImpl(workspaceApi, sessionManager)
    }
} 