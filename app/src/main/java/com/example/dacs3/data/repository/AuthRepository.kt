package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.AuthApi
import com.example.dacs3.data.local.dao.AccountDao
import com.example.dacs3.data.local.entity.AccountEntity
import com.example.dacs3.data.local.dao.UserDao
import com.example.dacs3.data.local.entity.UserEntity
import com.example.dacs3.data.model.*
import com.example.dacs3.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        try {
            val response = api.login(request)
            
            // If login successful, save to Room database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    response.account?.let { account ->
                        response.token?.let { token ->
                            // Save token and user info in session manager
                            val userId = UUID.randomUUID().toString() // Generate a temporary ID if needed
                            sessionManager.saveUserSession(userId, account.email,account.contactNumber, account.username, token)
                            sessionManager.saveToken(token)
                        }
                    }
                }
            }
            return response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            throw e
        }
    }
    
    suspend fun register(request: RegisterRequest): RegisterResponse {
        try {
            val response = api.register(request)
            
            // If registration successful, save to Room database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    response.account?.let { account ->
                            Log.d("AuthRepository", "Successfully saved user data locally")
                    }
                }
            }
            return response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Register error", e)
            throw e
        }
    }
    
    suspend fun forgotPassword(email: String): ForgotPasswordResponse {
        try {
            val request = ForgotPasswordRequest(email)
            return api.forgotPassword(request)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Forgot password error", e)
            throw e
        }
    }
    
    suspend fun resetPassword(email: String, password: String, otp: String): ResetPasswordResponse {
        try {
            val request = ResetPasswordRequest(email, password, otp)
            val response = api.resetPassword(request)
            
            return response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Reset password error", e)
            throw e
        }
    }
    
    suspend fun verifyEmail(email: String, otp: String): VerifyEmailResponse {
        try {
            val request = VerifyEmailRequest(email, otp)
            val response = api.verifyEmail(request)
            
            return response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Verify email error", e)
            throw e
        }
    }
    
    suspend fun resendOtp(email: String, forVerification: Boolean = true): ResendOtpResponse {
        try {
            val request = ResendOtpRequest(email, forVerification)
            return api.resendOtp(request)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Resend OTP error", e)
            throw e
        }
    }
    
    suspend fun verifyOtp(email: String, otp: String, deviceID: String?): VerifyOtpResponse {
        try {
            val request = VerifyOtpRequest(email, otp, deviceID ?: "")
            return api.verifyOtp(request)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Verify OTP error", e)
            throw e
        }
    }
}
