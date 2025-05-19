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
        TODO()
    }
    
    override suspend fun getById(id: String): UserEntity? {
        TODO()
    }
    
    override suspend fun insert(item: UserEntity) {
        TODO()
    }
    
    override suspend fun insertAll(items: List<UserEntity>) {
        TODO()
    }
    
    override suspend fun update(item: UserEntity) {
        TODO()
    }
    
    override suspend fun delete(item: UserEntity) {
        TODO()
    }
    
    override suspend fun deleteById(id: String) {
        TODO()
    }
    
    override suspend fun deleteAll() {
        TODO()
    }
    
    override suspend fun sync() {
        TODO()
    }
    
    override suspend fun getAllUsersFromApi(page: Int?, limit: Int?): UserListResponse {
        return try {
            val response = userApi.getAllUsers(page, limit)

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

            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user", e)
            false
        }
    }
    
    override suspend fun searchUsersFromApi(query: String): UserListResponse {
        return try {
            val response = userApi.searchUsers(query)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error searching users", e)
            // Trả về response rỗng với success=false khi API thất bại
            UserListResponse(false, 0, 0, emptyList())
        }
    }
}