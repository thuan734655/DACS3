package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Message
import retrofit2.Response
import retrofit2.http.*

interface MessagesApi {
    @GET("messages/channel/{channelId}")
    suspend fun getChannelMessages(
        @Path("channelId") channelId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<List<Message>>>
    
    @GET("messages/direct/{userId}")
    suspend fun getDirectMessages(
        @Path("userId") userId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<List<Message>>>
    
    @POST("messages/channel/{channelId}")
    suspend fun sendChannelMessage(
        @Path("channelId") channelId: String,
        @Body message: Message
    ): Response<ApiResponse<Message>>
    
    @POST("messages/direct/{userId}")
    suspend fun sendDirectMessage(
        @Path("userId") userId: String,
        @Body message: Message
    ): Response<ApiResponse<Message>>
    
    @DELETE("messages/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: String): Response<ApiResponse<Any>>
    
    @PUT("messages/{messageId}")
    suspend fun editMessage(
        @Path("messageId") messageId: String,
        @Body message: Message
    ): Response<ApiResponse<Message>>
} 