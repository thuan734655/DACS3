package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.OtpApi
import com.example.dacs3.data.local.dao.AccountDao
import com.example.dacs3.data.model.ResendOtpRequest
import com.example.dacs3.data.model.ResendOtpResponse
import com.example.dacs3.data.model.VerifyOtpRequest
import com.example.dacs3.data.model.VerifyOtpResponse
import com.example.dacs3.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpRepository @Inject constructor(
    private val otpApi: OtpApi,
    private val sessionManager: SessionManager
) {
    suspend fun verifyOtp(email: String, otp: String, deviceId: String? = null): VerifyOtpResponse {
        return try {
            Log.d("OtpRepository", "Creating verification request with deviceId: $deviceId")
            val request = VerifyOtpRequest(email, otp, deviceId ?: "")
            Log.d("OtpRepository", "Sending OTP verification request: $request")
            val response = otpApi.verifyOtp(request)

            
            response
        } catch (e: Exception) {
            Log.e("OtpRepository", "Error verifying OTP", e)
            throw e
        }
    }
    
    suspend fun resendOtp(email: String, forVerification: Boolean): ResendOtpResponse {
        return try {
            val request = ResendOtpRequest(email, forVerification)
            otpApi.resendOtp(request)
        } catch (e: Exception) {
            throw e
        }
    }
    
    private suspend fun updateEmailVerification(email: String, isVerified: Boolean) {
        try {
            // Log the action for debugging purposes
            Log.d("OtpRepository", "Updating email verification status for $email to $isVerified")
            
            // This could involve updating the local database or making an API call
            // For now, we'll just log it since the auto-login process doesn't require this step
            // to be fully implemented
            
            // No implementation is needed right now as we're using the OTP verification response
            // to trigger the auto-login directly in the OtpViewModel
        } catch (e: Exception) {
            Log.e("OtpRepository", "Error updating email verification", e)
            // Don't throw the exception to prevent app crashes
        }
    }
}