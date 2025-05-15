package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface ChannelApi {
    // GET all channels with pagination
    @GET("channels")
    suspend fun getAllChannels(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null
    ): ChannelListResponse

    // GET channel by ID
    @GET("channels/{id}")
    suspend fun getChannelById(@Path("id") id: String): ChannelResponse

    // POST create new channel
    @POST("channels")
    suspend fun createChannel(@Body request: CreateChannelRequest): ChannelResponse

    // PUT update channel
    @PUT("channels/{id}")
    suspend fun updateChannel(
        @Path("id") id: String,
        @Body request: UpdateChannelRequest
    ): ChannelResponse

    // DELETE channel
    @DELETE("channels/{id}")
    suspend fun deleteChannel(@Path("id") id: String): MessageResponse

    // PUT add member to channel
    @PUT("channels/{id}/members")
    suspend fun addMember(
        @Path("id") id: String,
        @Body request: AddMemberRequest
    ): ChannelResponse

    // DELETE remove member from channel
    @DELETE("channels/{id}/members/{userId}")
    suspend fun removeMember(
        @Path("id") id: String,
        @Path("userId") userId: String
    ): ChannelResponse

    // POST join a channel
    @POST("channels/{id}/join")
    suspend fun joinChannel(@Path("id") id: String): ChannelResponse

    // DELETE leave a channel
    @DELETE("channels/{id}/leave")
    suspend fun leaveChannel(@Path("id") id: String): MessageResponse
} 