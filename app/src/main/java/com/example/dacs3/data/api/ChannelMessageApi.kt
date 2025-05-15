package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface ChannelMessageApi {
    // GET all messages for a specific channel
    @GET("channel-messages/{channelId}")
    suspend fun getChannelMessages(
        @Path("channelId") channelId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): MessageListResponse

    // POST send a new message to a channel
    @POST("channel-messages/{channelId}")
    suspend fun sendChannelMessage(
        @Path("channelId") channelId: String,
        @Body request: SendChannelMessageRequest
    ): MessageResponse

    // GET thread replies for a channel message
    @GET("channel-messages/threads/{messageId}")
    suspend fun getChannelThreadReplies(
        @Path("messageId") messageId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ThreadRepliesResponse

    // POST reply to a thread in a channel
    @POST("channel-messages/threads/{messageId}")
    suspend fun replyToChannelThread(
        @Path("messageId") messageId: String,
        @Body request: SendChannelMessageRequest
    ): MessageResponse
} 