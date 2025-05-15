package com.example.dacs3.di

import com.example.dacs3.data.repository.ChannelRepository
import com.example.dacs3.data.repository.NotificationRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.repository.impl.ChannelRepositoryImpl
import com.example.dacs3.data.repository.impl.NotificationRepositoryImpl
import com.example.dacs3.data.repository.impl.WorkspaceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkspaceRepository(
        workspaceRepositoryImpl: WorkspaceRepositoryImpl
    ): WorkspaceRepository

    @Binds
    @Singleton
    abstract fun bindChannelRepository(
        channelRepositoryImpl: ChannelRepositoryImpl
    ): ChannelRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
} 