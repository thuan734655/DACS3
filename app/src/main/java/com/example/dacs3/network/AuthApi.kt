package com.example.dacs3.network

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @POST("api/otp/resend")
    suspend fun resendOtp(@Body req: ResendOtpRequest): ResendOtpResponse

    @POST("api/otp/verify")
    suspend fun verifyOtp(@Body req: VerifyOtpRequest): VerifyOtpResponse

}
