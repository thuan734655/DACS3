package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.UserApi
import com.example.dacs3.data.local.dao.UserDao
import com.example.dacs3.data.local.entity.UserEntity
import com.example.dacs3.data.model.CreateUserRequest
import com.example.dacs3.data.model.UpdateUserRequest
import com.example.dacs3.data.model.UserListResponse
import com.example.dacs3.data.model.UserResponse
import com.example.dacs3.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi
) : UserRepository {
    
    private val TAG = "UserRepositoryImpl"
    
    override fun getAll(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }
    
    override suspend fun getById(id: String): UserEntity? {
        return userDao.getUserById(id)
    }
    
    override suspend fun insert(item: UserEntity) {
        userDao.insertUser(item)
    }
    
    override suspend fun insertAll(items: List<UserEntity>) {
        userDao.insertUsers(items)
    }
    
    override suspend fun update(item: UserEntity) {
        userDao.updateUser(item)
    }
    
    override suspend fun delete(item: UserEntity) {
        userDao.deleteUser(item)
    }
    
    override suspend fun deleteById(id: String) {
        userDao.deleteUserById(id)
    }
    
    override suspend fun deleteAll() {
        userDao.deleteAllUsers()
    }
    
    override suspend fun sync() {
        try {
            val response = userApi.getAllUsers()
            if (response.success && response.data != null) {
                val users = response.data.map { UserEntity.fromUser(it) }
                userDao.insertUsers(users)
                Log.d(TAG, "Successfully synced ${users.size} users")
            } else {
                Log.w(TAG, "Failed to sync users")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing users", e)
        }
    }
    
    override suspend fun getAllUsersFromApi(page: Int?, limit: Int?): UserListResponse {
        return try {
            val response = userApi.getAllUsers(page, limit)
            
            // If successful, store users in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val userEntities = response.data.map { UserEntity.fromUser(it) }
                    userDao.insertUsers(userEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching users from API", e)
            // Return empty response with success=false when API fails
            UserListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getUserByIdFromApi(id: String): UserResponse {
        return try {
            val response = userApi.getUserById(id)
            
            // If successful, store user in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val userEntity = UserEntity.fromUser(response.data)
                    userDao.insertUser(userEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user from API", e)
            // Return empty response with success=false when API fails
            UserResponse(false, null)
        }
    }
    
    override suspend fun createUser(name: String, avatar: String?): UserResponse {
        return try {
            val request = CreateUserRequest(name, avatar)
            val response = userApi.createUser(request)
            
            // If successful, store user in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val userEntity = UserEntity.fromUser(response.data)
                    userDao.insertUser(userEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user", e)
            // Return empty response with success=false when API fails
            UserResponse(false, null)
        }
    }
    
    override suspend fun updateUser(id: String, name: String?, avatar: String?): UserResponse {
        return try {
            val request = UpdateUserRequest(name, avatar)
            val response = userApi.updateUser(id, request)
            
            // If successful, update user in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val userEntity = UserEntity.fromUser(response.data)
                    userDao.updateUser(userEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user", e)
            // Return empty response with success=false when API fails
            UserResponse(false, null)
        }
    }
    
    override suspend fun deleteUserFromApi(id: String): Boolean {
        return try {
            val response = userApi.deleteUser(id)
            
            // If successful, delete user from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    userDao.deleteUserById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user", e)
            false
        }
    }
} 