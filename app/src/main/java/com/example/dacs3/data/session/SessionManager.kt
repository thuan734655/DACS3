package com.example.dacs3.data.session

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date

/**
 * SessionManager - Handles user authentication state persistence
 */
@Singleton
class SessionManager @Inject constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    
    companion object {
        private const val PREFS_NAME = "dacs3_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TOKEN = "user_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry_time"
        private const val KEY_FIRST_TIME = "is_first_time"
    }
    
    /**
     * Save user session when they log in successfully
     */
    fun saveUserSession(userId: String, email: String, token: String) {
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_TOKEN, token)
        
        // Calculate expiration time (168 hours from now)
        val expiryTime = System.currentTimeMillis() + (168 * 60 * 60 * 1000)
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
        
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
        
        Log.d("SessionManager", "Saved user session with token expiry: ${Date(expiryTime)}")
    }
    
    /**
     * Check if user is logged in with a valid token
     */
    fun isLoggedIn(): Boolean {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return false
        
        // Check if token exists and is not expired
        val token = prefs.getString(KEY_TOKEN, null)
        val tokenExpiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        val currentTime = System.currentTimeMillis()
        
        // If token is missing or expired, clear session and return false
        if (token == null || currentTime > tokenExpiryTime) {
            Log.d("SessionManager", "Token expired or missing. Clearing session.")
            clearSession()
            return false
        }
        
        return true
    }
    
    /**
     * Get remaining time until token expires (in milliseconds)
     */
    fun getTokenRemainingTime(): Long {
        val tokenExpiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        val currentTime = System.currentTimeMillis()
        return if (tokenExpiryTime > currentTime) {
            tokenExpiryTime - currentTime
        } else 0
    }
    
    /**
     * Get logged in user id
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    /**
     * Get logged in user email
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }
    
    /**
     * Clear session details
     */
    fun clearSession() {
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_EMAIL)
        editor.remove(KEY_TOKEN)
        editor.remove(KEY_TOKEN_EXPIRY)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
    }
    
    fun saveAuthToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        
        // Calculate expiration time (168 hours from now)
        val expiryTime = System.currentTimeMillis() + (168 * 60 * 60 * 1000)
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
        
        editor.apply()
    }

    fun getAuthToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)
        val tokenExpiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        val currentTime = System.currentTimeMillis()
        
        // Return null if token is expired
        return if (token != null && currentTime <= tokenExpiryTime) {
            token
        } else null
    }

    fun isFirstTimeUser(): Boolean {
        return prefs.getBoolean(KEY_FIRST_TIME, true)
    }

    fun setFirstTimeDone() {
        editor.putBoolean(KEY_FIRST_TIME, false)
        editor.apply()
    }
} 