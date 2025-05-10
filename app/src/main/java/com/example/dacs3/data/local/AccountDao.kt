package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE userId = :userId")
    suspend fun getAccountByUserId(userId: String): AccountEntity?
    
    @Query("SELECT * FROM accounts WHERE email = :email")
    suspend fun getAccountByEmail(email: String): AccountEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)
    
    @Update
    suspend fun updateAccount(account: AccountEntity)
    
    @Delete
    suspend fun deleteAccount(account: AccountEntity)
    
    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountCount(): Int
} 