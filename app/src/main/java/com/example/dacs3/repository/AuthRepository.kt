package com.example.dacs3.repository

import com.example.dacs3.network.AuthApi
import com.example.dacs3.network.LoginRequest
import com.example.dacs3.network.LoginResponse
import com.example.dacs3.network.RegisterRequest
import com.example.dacs3.network.RegisterResponse
import com.example.dacs3.network.ResendOtpRequest
import com.example.dacs3.network.ResendOtpResponse
import com.example.dacs3.network.VerifyOtpRequest
import com.example.dacs3.network.VerifyOtpResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi
) {
    suspend fun register(req: RegisterRequest): Result<RegisterResponse> =
        runCatching { api.register(req) }

    suspend fun login(req: LoginRequest): Result<LoginResponse> =
        runCatching { api.login(req) }

    suspend fun resendOtp(email: String): Result<ResendOtpResponse> =
        runCatching { api.resendOtp(ResendOtpRequest(email)) }

    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpResponse> =
        runCatching { api.verifyOtp(VerifyOtpRequest(email, otp)) }

}
