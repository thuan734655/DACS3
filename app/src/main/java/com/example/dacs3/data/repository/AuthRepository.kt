package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.AuthApi
import com.example.dacs3.data.local.AccountDao
import com.example.dacs3.data.local.AccountEntity
import com.example.dacs3.data.local.UserDao
import com.example.dacs3.data.local.UserEntity
import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.ForgotPasswordRequest
import com.example.dacs3.data.model.LoginRequest
import com.example.dacs3.data.model.RegisterRequest
import com.example.dacs3.data.model.ResetPasswordRequest
import com.example.dacs3.data.model.VerifyEmailRequest
import com.example.dacs3.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
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
    suspend fun login(req: LoginRequest): Response<AuthResponse> {
        try {
            val response = api.login(req)
            
            // If login successful, save to Room database
            if (response.isSuccessful && response.body()?.success == true) {
                withContext(Dispatchers.IO) {
                    response.body()?.let { authResponse ->
                        authResponse.account?.let { account ->
                            // Generate a local user ID if none exists
                            val userId = UUID.randomUUID().toString()
                            val accountId = UUID.randomUUID().toString()
                            
                            // Check for null values before creating entities
                            if (account.username != null && account.email != null) {
                                // Save user info
                                val userEntity = UserEntity(
                                    userId = userId,
                                    username = account.username,
                                    email = account.email,
                                    password = "",  // We don't store actual password
                                    avatarUrl = null,
                                    isOnline = true
                                )
                                userDao.insertUser(userEntity)
                                
                                // Save account info
                                val accountEntity = AccountEntity(
                                    accountId = accountId,
                                    email = account.email,
                                    contactNumber = account.contactNumber ?: "",
                                    password = "",  // We don't store actual password
                                    isEmailVerified = true,
                                    deviceId = req.deviceID,
                                    userId = userId
                                )
                                accountDao.insertAccount(accountEntity)
                                
                                // Save session info in shared prefs
                                authResponse.token?.let { token ->
                                    sessionManager.saveUserSession(userId, account.email, token)
                                }
                            } else {
                                Log.e("AuthRepository", "Login success but username or email is null")
                            }
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
    
    suspend fun register(req: RegisterRequest): Response<AuthResponse> {
        try {
            val response = api.register(req)
            
            // If registration successful, save to Room database
            if (response.isSuccessful && response.body()?.success == true) {
                withContext(Dispatchers.IO) {
                    response.body()?.let { authResponse ->
                        authResponse.account?.let { account ->
                            try {
                                // Generate a local user ID if none exists
                                val userId = UUID.randomUUID().toString()
                                val accountId = UUID.randomUUID().toString()
                                
                                // Check for null values and use request data as fallback
                                val username = account.username ?: req.username ?: throw IllegalArgumentException("Username cannot be null")
                                val email = account.email ?: req.email ?: throw IllegalArgumentException("Email cannot be null")
                                
                                // Save user info
                                val userEntity = UserEntity(
                                    userId = userId,
                                    username = username,
                                    email = email,
                                    password = "",  // We don't store actual password
                                    avatarUrl = null
                                )
                                userDao.insertUser(userEntity)
                                
                                // Save account info but mark as not verified
                                val accountEntity = AccountEntity(
                                    accountId = accountId,
                                    email = email,
                                    contactNumber = account.contactNumber ?: req.contactNumber ?: "",
                                    password = "",  // We don't store actual password
                                    isEmailVerified = false,
                                    userId = userId
                                )
                                accountDao.insertAccount(accountEntity)
                                
                                Log.d("AuthRepository", "Successfully saved user data locally")
                            } catch (e: Exception) {
                                Log.e("AuthRepository", "Error saving user data locally", e)
                                // Continue with response - don't fail the whole registration
                                // if local data storage fails
                            }
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
    
    suspend fun getLocalUserByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            val account = accountDao.getAccountByEmail(email)
            account?.let {
                userDao.getUserById(it.userId)
            }
        }
    }
    
    suspend fun updateEmailVerification(email: String, isVerified: Boolean) {
        withContext(Dispatchers.IO) {
            val account = accountDao.getAccountByEmail(email)
            account?.let {
                accountDao.updateAccount(it.copy(isEmailVerified = isVerified))
            }
        }
    }
    
    suspend fun updateDeviceVerification(email: String, isVerified: Boolean) {
        withContext(Dispatchers.IO) {
            val account = accountDao.getAccountByEmail(email)
            account?.let {
                // In a real app, you would store the current device ID that was verified
                // For demo, we just update a flag in the account entity
                accountDao.updateAccount(it.copy(isDeviceVerified = true))
                
                Log.d("AuthRepository", "Device verification updated for email: $email")
            }
        }
    }
    
    suspend fun forgotPassword(email: String): Response<AuthResponse> {
        try {
            val request = ForgotPasswordRequest(email)
            return api.forgotPassword(request)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Forgot password error", e)
            throw e
        }
    }
    
    suspend fun resetPassword(email: String, password: String, otp: String): Response<AuthResponse> {
        try {
            val request = ResetPasswordRequest(email, password, otp)
            val response = api.resetPassword(request)
            
            // If reset is successful, update local password
            if (response.isSuccessful && response.body()?.success == true) {
                updateLocalUserPassword(email, password)
            }
            
            return response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Reset password error", e)
            throw e
        }
    }
    
    private suspend fun updateLocalUserPassword(email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val account = accountDao.getAccountByEmail(email)
                account?.let {
                    // Note: In real app we'd encrypt the password
                    // Here we're just storing a placeholder
                    accountDao.updateAccount(it.copy(password = ""))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error updating local password", e)
                // Continue even if local update fails
            }
        }
    }
    
    suspend fun verifyEmail(email: String, otp: String): Response<AuthResponse> {
        try {
            val request = VerifyEmailRequest(email, otp)
            return api.verifyEmail(request)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Verify email error", e)
            throw e
        }
    }
}
