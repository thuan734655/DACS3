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
    private val accountDao: AccountDao,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        try {
            val response = api.login(request)
            
            // If login successful, save to Room database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    response.account?.let { account ->
                        // Save user info
                        val user = userDao.getUserById(account.username)
                        
                        if (user == null && account.email != null) {
                            // If user doesn't exist locally, create it
                            val userEntity = UserEntity(
                                _id = UUID.randomUUID().toString(),
                                name = account.username,
                                avatar = null,
                                created_at = Date()
                            )
                            userDao.insertUser(userEntity)
                            
                            // Save account info
                            val accountEntity = AccountEntity(
                                _id = UUID.randomUUID().toString(),
                                email = account.email,
                                contactNumber = account.contactNumber,
                                password = "",  // We don't store actual password
                                otp = null,
                                create_at_otp = null,
                                verifyMail = true,
                                deviceID = request.deviceID,
                                user_id = userEntity._id
                            )
                            accountDao.insertAccount(accountEntity)
                        }
                        
                        // Save session info
                        response.token?.let { token ->
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
                        try {
                            // Save user info
                            val userEntity = UserEntity(
                                _id = UUID.randomUUID().toString(),
                                name = account.username,
                                avatar = null,
                                created_at = Date()
                            )
                            userDao.insertUser(userEntity)
                            
                            // Save account info but mark as not verified
                            val accountEntity = AccountEntity(
                                _id = UUID.randomUUID().toString(),
                                email = account.email,
                                contactNumber = account.contactNumber,
                                password = "",  // We don't store actual password
                                otp = null,
                                create_at_otp = null,
                                verifyMail = false,
                                deviceID = null,
                                user_id = userEntity._id
                            )
                            accountDao.insertAccount(accountEntity)
                            
                            Log.d("AuthRepository", "Successfully saved user data locally")
                        } catch (e: Exception) {
                            Log.e("AuthRepository", "Error saving user data locally", e)
                        }
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
            
            // If reset is successful, update local password
            if (response.success) {
                withContext(Dispatchers.IO) {
                    try {
                        accountDao.updatePassword(email, "")
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Error updating local password", e)
                    }
                }
            }
            
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
            
            if (response.success) {
                withContext(Dispatchers.IO) {
                    try {
                        accountDao.updateEmailVerification(email, true)
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Error updating email verification", e)
                    }
                }
            }
            
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
