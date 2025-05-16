package com.example.dacs3.data.user

import com.example.dacs3.data.model.User
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserManager - Responsible for user-related operations and retrieving current user info
 */
@Singleton
class UserManager @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) {
    /**
     * Get the current user's ID from the session
     */
    fun getCurrentUserId(): String? {
        return sessionManager.getUserId()
    }
    
    /**
     * Get the current user's email from the session
     */
    fun getCurrentUserEmail(): String? {
        return sessionManager.getUserEmail()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    /**
     * Get the current logged-in user
     * Note: This is a blocking call and should be used carefully
     */
    fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return runBlocking {
            val response = userRepository.getUserByIdFromApi(userId)
            if (response.success) response.data else null
        }
    }
} 