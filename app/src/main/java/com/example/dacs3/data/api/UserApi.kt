package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface UserApi {
    // GET all users with pagination
    @GET("users")
    suspend fun getAllUsers(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): UserListResponse

    // GET user by ID
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserResponse

    // POST create new user
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): UserResponse

    // PUT update user
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): UserResponse

    // DELETE user
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): MessageResponse
}