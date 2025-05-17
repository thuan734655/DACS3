package com.example.dacs3.di

import com.example.dacs3.data.api.*
import com.example.dacs3.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        sessionManager: SessionManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = sessionManager.getToken()?.let { token ->
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } ?: chain.request()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): com.google.gson.Gson {
        return com.google.gson.GsonBuilder()
            .registerTypeAdapter(
                com.example.dacs3.data.model.Epic::class.java,
                com.example.dacs3.data.api.deserializer.EpicDeserializer()
            )
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: com.google.gson.Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
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
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkspaceApi(retrofit: Retrofit): WorkspaceApi {
        return retrofit.create(WorkspaceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChannelApi(retrofit: Retrofit): ChannelApi {
        return retrofit.create(ChannelApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChannelMessageApi(retrofit: Retrofit): ChannelMessageApi {
        return retrofit.create(ChannelMessageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDirectMessageApi(retrofit: Retrofit): DirectMessageApi {
        return retrofit.create(DirectMessageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMessageApi(retrofit: Retrofit): MessageApi {
        return retrofit.create(MessageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApi {
        return retrofit.create(TaskApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBugApi(retrofit: Retrofit): BugApi {
        return retrofit.create(BugApi::class.java)
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
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportDailyApi(retrofit: Retrofit): ReportDailyApi {
        return retrofit.create(ReportDailyApi::class.java)
    }
}