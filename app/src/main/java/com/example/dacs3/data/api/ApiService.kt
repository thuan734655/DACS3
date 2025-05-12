package com.example.dacs3.data.api

import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("veify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<AuthResponse>
} 