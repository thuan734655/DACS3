package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.UserEntity
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.UserListResponse
import com.example.dacs3.data.model.UserResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface UserRepository : BaseRepository<UserEntity, String> {
    /**
     * Get all users from remote API with pagination
     */
    suspend fun getAllUsersFromApi(page: Int? = null, limit: Int? = null): UserListResponse
    
    /**
     * Get user by ID from remote API
     */
    suspend fun getUserByIdFromApi(id: String): UserResponse
    
    /**
     * Create a new user on the remote API
     */
    suspend fun createUser(name: String, avatar: String?): UserResponse
    
    /**
     * Update a user on the remote API
     */
    suspend fun updateUser(id: String, name: String?, avatar: String?): UserResponse
    
    /**
     * Delete a user on the remote API
     */
    suspend fun deleteUserFromApi(id: String): Boolean
    
    suspend fun searchUsersFromApi(query: String): UserListResponse
}