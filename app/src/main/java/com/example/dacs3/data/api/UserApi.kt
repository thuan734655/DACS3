package com.example.dacs3.data.api

import com.example.dacs3.data.model.CreateUserRequest
import com.example.dacs3.data.model.MessageResponse
import com.example.dacs3.data.model.UpdateUserRequest
import com.example.dacs3.data.model.UserListResponse
import com.example.dacs3.data.model.UserResponse
import com.example.dacs3.data.model.ProfileResponse
import com.example.dacs3.data.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("users/profile")
    suspend fun getProfile(): Response<ProfileResponse>
    
    @PUT("users/profile")
    suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): Response<ProfileResponse>
    
    // DELETE user
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): MessageResponse
    
    suspend fun searchUsers(query: String): UserListResponse
}