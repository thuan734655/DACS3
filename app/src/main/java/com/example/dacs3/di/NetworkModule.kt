package com.example.dacs3.di

import com.example.dacs3.data.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOtpApi(retrofit: Retrofit): OtpApi {
        return retrofit.create(OtpApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChannelApi(retrofit: Retrofit): ChannelApi {
        return retrofit.create(ChannelApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkspaceApi(retrofit: Retrofit): WorkspaceApi {
        return retrofit.create(WorkspaceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMessagesApi(retrofit: Retrofit): MessagesApi {
        return retrofit.create(MessagesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApi {
        return retrofit.create(TaskApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEpicApi(retrofit: Retrofit): EpicApi {
        return retrofit.create(EpicApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSprintApi(retrofit: Retrofit): SprintApi {
        return retrofit.create(SprintApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportApi(retrofit: Retrofit): ReportApi {
        return retrofit.create(ReportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }
} 