package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
//    @Query("SELECT * FROM users")
//    fun getAllUsers(): Flow<List<UserEntity>>
//
//    @Query("SELECT * FROM users WHERE _id = :id")
//    suspend fun getUserById(id: String): UserEntity?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUser(user: UserEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUsers(users: List<UserEntity>)
//
//    @Update
//    suspend fun updateUser(user: UserEntity)
//
//    @Delete
//    suspend fun deleteUser(user: UserEntity)
//
//    @Query("DELETE FROM users WHERE _id = :id")
//    suspend fun deleteUserById(id: String)
//
//    @Query("DELETE FROM users")
//    suspend fun deleteAllUsers()
} 