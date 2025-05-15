package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface OtpApi {
    // POST verify OTP
    @POST("otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): VerifyOtpResponse

    // POST resend OTP
    @POST("otp/resend")
    suspend fun resendOtp(@Body request: ResendOtpRequest): ResendOtpResponse
} 