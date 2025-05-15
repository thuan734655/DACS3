package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface DirectMessageApi {
    // GET all conversations for the current user
    @GET("direct-messages/conversations")
    suspend fun getConversations(): ConversationResponse

    // GET message history with a specific user
    @GET("direct-messages/{userId}")
    suspend fun getMessages(
        @Path("userId") userId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): MessageListResponse

    // POST send a new direct message to a user
    @POST("direct-messages/{userId}")
    suspend fun sendMessage(
        @Path("userId") userId: String,
        @Body request: SendDirectMessageRequest
    ): MessageResponse

    // GET thread replies for a direct message
    @GET("direct-messages/threads/{messageId}")
    suspend fun getThreadReplies(
        @Path("messageId") messageId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ThreadRepliesResponse

    // POST reply to a thread in a direct message
    @POST("direct-messages/threads/{messageId}")
    suspend fun replyToThread(
        @Path("messageId") messageId: String,
        @Body request: SendDirectMessageRequest
    ): MessageResponse
}