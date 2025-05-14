package com.example.dacs3.data.api

import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface ChannelApi {
    @GET("channel")
    suspend fun getAllChannels(): Response<ApiResponse<List<Channel>>>
    
    @GET("channel/{channelId}")
    suspend fun getChannelById(@Path("channelId") channelId: String): Response<ApiResponse<Channel>>
    
    @POST("channel")
    suspend fun createChannel(@Body channel: Channel): Response<ApiResponse<Channel>>
    
    @PUT("channel/{channelId}")
    suspend fun updateChannel(
        @Path("channelId") channelId: String,
        @Body channel: Channel
    ): Response<ApiResponse<Channel>>
    
    @DELETE("channel/{channelId}")
    suspend fun deleteChannel(@Path("channelId") channelId: String): Response<ApiResponse<Any>>
    
    @POST("channel/{channelId}/member")
    suspend fun addMember(
        @Path("channelId") channelId: String,
        @Body memberData: Map<String, String>
    ): Response<ApiResponse<Channel>>
    
    @DELETE("channel/{channelId}/member/{memberId}")
    suspend fun removeMember(
        @Path("channelId") channelId: String,
        @Path("memberId") memberId: String
    ): Response<ApiResponse<Channel>>
} 