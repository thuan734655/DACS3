package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.local.dao.AccountDao
import com.example.dacs3.data.local.entity.AccountEntity
import com.example.dacs3.data.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {
    
    private val TAG = "AccountRepositoryImpl"
    
    override fun getAll(): Flow<List<AccountEntity>> {
        return accountDao.getAllAccounts()
    }
    
    override suspend fun getById(id: String): AccountEntity? {
        return accountDao.getAccountById(id)
    }
    
    override suspend fun insert(item: AccountEntity) {
        accountDao.insertAccount(item)
    }
    
    override suspend fun insertAll(items: List<AccountEntity>) {
        items.forEach { accountDao.insertAccount(it) }
    }
    
    override suspend fun update(item: AccountEntity) {
        accountDao.updateAccount(item)
    }
    
    override suspend fun delete(item: AccountEntity) {
        accountDao.deleteAccount(item)
    }
    
    override suspend fun deleteById(id: String) {
        accountDao.deleteAccountById(id)
    }
    
    override suspend fun deleteAll() {
        // This operation is not implemented for accounts as it's a critical operation
        Log.w(TAG, "deleteAll operation is not implemented for accounts")
    }
    
    override suspend fun sync() {
        // Account data is synced during login/registration operations
        // No additional sync is needed for offline-first approach
        Log.d(TAG, "Account sync is handled through Auth operations")
    }
    
    override suspend fun getAccountByEmail(email: String): AccountEntity? {
        return accountDao.getAccountByEmail(email)
    }
    
    override suspend fun getAccountByContactNumber(contactNumber: String): AccountEntity? {
        return accountDao.getAccountByContactNumber(contactNumber)
    }
    
    override suspend fun getAccountByUserId(userId: String): AccountEntity? {
        return accountDao.getAccountByUserId(userId)
    }
    
    override suspend fun updateOtp(email: String, otp: String, createAtOtp: Date) {
        accountDao.updateOtp(email, otp, createAtOtp)
    }
    
    override suspend fun updateEmailVerification(email: String, verified: Boolean) {
        accountDao.updateEmailVerification(email, verified)
    }
    
    override suspend fun updateDeviceId(email: String, deviceId: String) {
        accountDao.updateDeviceId(email, deviceId)
    }
    
    override suspend fun updatePassword(email: String, newPassword: String) {
        accountDao.updatePassword(email, newPassword)
    }
} 