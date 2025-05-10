package com.example.dacs3.data.api

import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.LoginRequest
import com.example.dacs3.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
} 