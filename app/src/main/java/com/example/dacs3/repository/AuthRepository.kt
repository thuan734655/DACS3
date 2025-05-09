package com.example.dacs3.repository

import com.example.dacs3.models.HomeResponse
import com.example.dacs3.network.AuthApi
import com.example.dacs3.models.LoginRequest
import com.example.dacs3.models.LoginResponse
import com.example.dacs3.models.RegisterRequest
import com.example.dacs3.models.RegisterResponse
import com.example.dacs3.models.ResendOtpRequest
import com.example.dacs3.models.ResendOtpResponse
import com.example.dacs3.models.VerifyOtpRequest
import com.example.dacs3.models.VerifyOtpResponse
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi
) {
    suspend fun register(req: RegisterRequest): Result<RegisterResponse> =
        runCatching { api.register(req) }

    suspend fun login(req: LoginRequest): Result<LoginResponse> =
        runCatching { api.login(req) }

    suspend fun getHomeData(token: String): Response<HomeResponse> =
        api.getHomeData(token)

    suspend fun resendOtp(email: String): Result<ResendOtpResponse> =
        runCatching { api.resendOtp(ResendOtpRequest(email)) }

    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpResponse> =
        runCatching { api.verifyOtp(VerifyOtpRequest(email, otp)) }

}
