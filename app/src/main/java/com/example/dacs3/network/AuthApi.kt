package com.example.dacs3.network

import com.example.dacs3.models.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("api/home")
    suspend fun getHomeData(@Header("Authorization") bearer: String): Response<HomeResponse>

    @POST("api/otp/resend")
    suspend fun resendOtp(@Body req: ResendOtpRequest): ResendOtpResponse

    @POST("api/otp/verify")
    suspend fun verifyOtp(@Body req: VerifyOtpRequest): VerifyOtpResponse
}
