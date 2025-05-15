package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE _id = :id")
    suspend fun getAccountById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE email = :email")
    suspend fun getAccountByEmail(email: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE contactNumber = :contactNumber")
    suspend fun getAccountByContactNumber(contactNumber: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE user_id = :userId")
    suspend fun getAccountByUserId(userId: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("DELETE FROM accounts WHERE _id = :id")
    suspend fun deleteAccountById(id: String)

    @Query("UPDATE accounts SET otp = :otp, create_at_otp = :createAtOtp WHERE email = :email")
    suspend fun updateOtp(email: String, otp: String, createAtOtp: Date)

    @Query("UPDATE accounts SET verifyMail = :verified WHERE email = :email")
    suspend fun updateEmailVerification(email: String, verified: Boolean)

    @Query("UPDATE accounts SET deviceID = :deviceId WHERE email = :email")
    suspend fun updateDeviceId(email: String, deviceId: String)

    @Query("UPDATE accounts SET password = :newPassword WHERE email = :email")
    suspend fun updatePassword(email: String, newPassword: String)
} 