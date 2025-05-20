package com.example.dacs3.di

import com.example.dacs3.data.api.AuthApi
import com.example.dacs3.data.api.ChannelApi
import com.example.dacs3.data.api.ChatApi
import com.example.dacs3.data.api.EpicApi
import com.example.dacs3.data.api.InvitationApi
import com.example.dacs3.data.api.NotificationApi
import com.example.dacs3.data.api.OtpApi
import com.example.dacs3.data.api.SprintApi
import com.example.dacs3.data.api.TaskApi
import com.example.dacs3.data.api.UserApi
import com.example.dacs3.data.api.WorkspaceApi
import com.example.dacs3.data.api.deserializer.ChannelDeserializer
import com.example.dacs3.data.api.deserializer.EpicDeserializer
import com.example.dacs3.data.api.deserializer.InvitationDeserializer
import com.example.dacs3.data.api.deserializer.NotificationDeserializer
import com.example.dacs3.data.api.deserializer.WorkspaceDeserializer
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Epic
import com.example.dacs3.data.model.Invitation
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.session.SessionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor                                  // ← Thêm import này
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
            // interceptor log
            .addInterceptor(loggingInterceptor)
            // interceptor thêm header rõ kiểu để tránh ambiguity
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                sessionManager.getAuthToken()?.let { token ->
                    builder.addHeader("Authorization", "Bearer $token")
                }
                val request = builder.build()
                chain.proceed(request)
            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(object : TypeToken<List<Channel>>() {}.type, ChannelDeserializer())
            .registerTypeAdapter(Workspace::class.java, WorkspaceDeserializer())
            .registerTypeAdapter(Epic::class.java, EpicDeserializer())
            .registerTypeAdapter(Notification::class.java, NotificationDeserializer())
            .registerTypeAdapter(Invitation::class.java, InvitationDeserializer())
            .registerTypeAdapter(object : TypeToken<List<Notification>>() {}.type, JsonDeserializer { json, typeOfT, context ->
                val notifications = mutableListOf<Notification>()
                val jsonArray = json.asJsonArray
                for (element in jsonArray) {
                    notifications.add(NotificationDeserializer().deserialize(element, Notification::class.java, context))
                }
                notifications
            })
            .registerTypeAdapter(object : TypeToken<List<Invitation>>() {}.type, JsonDeserializer { json, typeOfT, context ->
                val invitations = mutableListOf<Invitation>()
                val jsonArray = json.asJsonArray
                for (element in jsonArray) {
                    invitations.add(InvitationDeserializer().deserialize(element, Invitation::class.java, context))
                }
                invitations
            })
            .setLenient()
            .create()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideOtpApi(retrofit: Retrofit): OtpApi =
        retrofit.create(OtpApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideWorkspaceApi(retrofit: Retrofit): WorkspaceApi =
        retrofit.create(WorkspaceApi::class.java)

    @Provides
    @Singleton
    fun provideChannelApi(retrofit: Retrofit): ChannelApi =
        retrofit.create(ChannelApi::class.java)

    @Provides
    @Singleton
    fun provideEpicApi(retrofit: Retrofit): EpicApi =
        retrofit.create(EpicApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApi =
        retrofit.create(TaskApi::class.java)

    @Provides
    @Singleton
    fun provideSprintApi(retrofit: Retrofit): SprintApi =
        retrofit.create(SprintApi::class.java)
        
    @Provides
    @Singleton
    fun provideInvitationApi(retrofit: Retrofit): InvitationApi {
        return retrofit.create(InvitationApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
    }    
    @Provides
    @Singleton
    fun provideWebSocketManager(gson: Gson): com.example.dacs3.data.websocket.WebSocketManager {
        return com.example.dacs3.data.websocket.WebSocketManager(gson)
    }

}
