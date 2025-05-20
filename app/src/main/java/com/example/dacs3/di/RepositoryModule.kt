package com.example.dacs3.di

import com.example.dacs3.data.repository.ChannelRepository
import com.example.dacs3.data.repository.EpicRepository
import com.example.dacs3.data.repository.InvitationRepository
import com.example.dacs3.data.repository.NotificationRepository
import com.example.dacs3.data.repository.SprintRepository
import com.example.dacs3.data.repository.TaskRepository
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.repository.impl.ChannelRepositoryImpl
import com.example.dacs3.data.repository.impl.EpicRepositoryImpl
import com.example.dacs3.data.repository.impl.InvitationRepositoryImpl
import com.example.dacs3.data.repository.impl.NotificationRepositoryImpl
import com.example.dacs3.data.repository.impl.SprintRepositoryImpl
import com.example.dacs3.data.repository.impl.TaskRepositoryImpl
import com.example.dacs3.data.repository.impl.UserRepositoryImpl
import com.example.dacs3.data.repository.impl.WorkspaceRepositoryImpl
import com.example.dacs3.di.RepositoryModule
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
    abstract fun bindEpicRepository(
        epicRepositoryImpl: EpicRepositoryImpl
    ): EpicRepository

    @Binds
    @Singleton
    abstract fun bindSprintRepository(
        sprintRepositoryImpl: SprintRepositoryImpl
    ): SprintRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

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
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindInvitationRepository(
        invitationRepositoryImpl: InvitationRepositoryImpl
    ): InvitationRepository

}
