package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface MessageApi {
    // GET all messages with pagination and filters
    @GET("messages")
    suspend fun getAllMessages(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("channel_id") channelId: String? = null,
        @Query("sender_id") senderId: String? = null,
        @Query("receiver_id") receiverId: String? = null,
        @Query("thread_parent_id") threadParentId: String? = null
    ): MessageListResponse

    // GET message by ID
    @GET("messages/{id}")
    suspend fun getMessageById(@Path("id") id: String): MessageResponse

    // POST create new message
    @POST("messages")
    suspend fun createMessage(@Body request: CreateMessageRequest): MessageResponse

    // PUT update message
    @PUT("messages/{id}")
    suspend fun updateMessage(
        @Path("id") id: String,
        @Body request: UpdateMessageRequest
    ): MessageResponse

    // DELETE message
    @DELETE("messages/{id}")
    suspend fun deleteMessage(@Path("id") id: String): MessageResponse

    // GET thread replies for a parent message
    @GET("messages/thread/{parentId}")
    suspend fun getThreadReplies(
        @Path("parentId") parentId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): MessageListResponse
} 