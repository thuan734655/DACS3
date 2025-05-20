package com.example.dacs3.data.api

import com.example.dacs3.data.model.ChatMemberListResponse
import com.example.dacs3.data.model.DirectMessageListResponse
import com.example.dacs3.data.model.DirectMessageRequest
import com.example.dacs3.data.model.DirectMessageResponse
import retrofit2.http.*

interface ChatApi {
    @GET("workspace-members/shared")
    suspend fun getUsersInSameWorkspaces(
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 20
    ): ChatMemberListResponse

    @GET("messages")
    suspend fun getDirectMessages(
        @Query("sender_id") senderId: String,
        @Query("receiver_id") receiverId: String,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 20
    ): DirectMessageListResponse

    @POST("messages")
    suspend fun sendDirectMessage(@Body message: DirectMessageRequest): DirectMessageResponse
}
