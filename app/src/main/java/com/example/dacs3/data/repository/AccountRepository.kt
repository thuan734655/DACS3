package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.AccountEntity
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface AccountRepository : BaseRepository<AccountEntity, String> {
    /**
     * Get account by email
     */
    suspend fun getAccountByEmail(email: String): AccountEntity?
    
    /**
     * Get account by contact number
     */
    suspend fun getAccountByContactNumber(contactNumber: String): AccountEntity?
    
    /**
     * Get account by user ID
     */
    suspend fun getAccountByUserId(userId: String): AccountEntity?
    
    /**
     * Update OTP for an account
     */
    suspend fun updateOtp(email: String, otp: String, createAtOtp: Date)
    
    /**
     * Update email verification status
     */
    suspend fun updateEmailVerification(email: String, verified: Boolean)
    
    /**
     * Update device ID for an account
     */
    suspend fun updateDeviceId(email: String, deviceId: String)
    
    /**
     * Update password for an account
     */
    suspend fun updatePassword(email: String, newPassword: String)
} 