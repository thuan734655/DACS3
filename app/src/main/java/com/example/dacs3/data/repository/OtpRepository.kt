package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.OtpApi
import com.example.dacs3.data.local.dao.AccountDao
import com.example.dacs3.data.model.ResendOtpRequest
import com.example.dacs3.data.model.ResendOtpResponse
import com.example.dacs3.data.model.VerifyOtpRequest
import com.example.dacs3.data.model.VerifyOtpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpRepository @Inject constructor(
    private val otpApi: OtpApi,
    private val accountDao: AccountDao
) {
    suspend fun verifyOtp(email: String, otp: String, deviceId: String? = null): VerifyOtpResponse {
        return try {
            Log.d("OtpRepository", "Creating verification request with deviceId: $deviceId")
            val request = VerifyOtpRequest(email, otp, deviceId ?: "")
            Log.d("OtpRepository", "Sending OTP verification request: $request")
            val response = otpApi.verifyOtp(request)
            
            // If verification successful, update local database
            if (response.success) {
                updateEmailVerification(email, true)
            }
            
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
        withContext(Dispatchers.IO) {
            try {
                accountDao.updateEmailVerification(email, isVerified)
            } catch (e: Exception) {
                Log.e("OtpRepository", "Error updating email verification", e)
            }
        }
    }
} 