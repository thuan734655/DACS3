package com.example.dacs3.network

import com.example.dacs3.models.LoginRequest
import com.example.dacs3.models.LoginResponse
import com.example.dacs3.models.RegisterRequest
import com.example.dacs3.models.RegisterResponse
import com.example.dacs3.models.ResendOtpRequest
import com.example.dacs3.models.ResendOtpResponse
import com.example.dacs3.models.VerifyOtpRequest
import com.example.dacs3.models.VerifyOtpResponse
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
