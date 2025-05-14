package com.example.dacs3.data.api

import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.ForgotPasswordRequest
import com.example.dacs3.data.model.LoginRequest
import com.example.dacs3.data.model.RegisterRequest
import com.example.dacs3.data.model.ResetPasswordRequest
import com.example.dacs3.data.model.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/forgotpassword")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<AuthResponse>
    
    @POST("auth/resetpassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<AuthResponse>
    
    @POST("auth/veify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<AuthResponse>
} 