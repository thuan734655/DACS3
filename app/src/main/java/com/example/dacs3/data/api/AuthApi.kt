package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface AuthApi {
    // POST register a new user
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    // POST login with email or phone
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // POST forgot password request
    @POST("auth/forgotpassword")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    // POST reset password with OTP
    @POST("auth/resetpassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    // POST verify email with OTP
    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): VerifyEmailResponse

    // POST resend OTP
    @POST("auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): ResendOtpResponse

    // POST verify OTP for 2FA
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): VerifyOtpResponse
} 