package com.example.dacs3.data.api

import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.OtpResendRequest
import com.example.dacs3.data.model.OtpVerificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpApi {
    @POST("otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<AuthResponse>
    
    @POST("otp/resend")
    suspend fun resendOtp(@Body request: OtpResendRequest): Response<AuthResponse>
} 