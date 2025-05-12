package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.OtpApi
import com.example.dacs3.data.local.AccountDao
import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.OtpResendRequest
import com.example.dacs3.data.model.OtpVerificationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpRepository @Inject constructor(
    private val otpApi: OtpApi,
    private val accountDao: AccountDao
) {
    suspend fun verifyOtp(email: String, otp: String, deviceId: String? = null): Response<AuthResponse> {
        return try {
            Log.d("OtpRepository", "Creating verification request with deviceId: $deviceId")
            val request = OtpVerificationRequest(email, otp, deviceId)
            Log.d("OtpRepository", "Sending OTP verification request: $request")
            val response = otpApi.verifyOtp(request)
            
            // If verification successful, update local database
            if (response.isSuccessful && response.body()?.success == true) {
                updateEmailVerification(email, true)
            }
            
            response
        } catch (e: Exception) {
            Log.e("OtpRepository", "Error verifying OTP", e)
            throw e
        }
    }
    
    suspend fun resendOtp(email: String): Response<AuthResponse> {
        return try {
            val request = OtpResendRequest(email)
            otpApi.resendOtp(request)
        } catch (e: Exception) {
            throw e
        }
    }
    
    private suspend fun updateEmailVerification(email: String, isVerified: Boolean) {
        withContext(Dispatchers.IO) {
            val account = accountDao.getAccountByEmail(email)
            account?.let {
                accountDao.updateAccount(it.copy(isEmailVerified = isVerified))
            }
        }
    }
} 