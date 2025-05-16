package com.example.dacs3.data.session

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionManagerViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel() {
    
    fun saveUserSession(userId: String, email: String, token: String) {
        sessionManager.saveUserSession(userId, email, token)
    }

    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun getTokenRemainingTime(): Long {
        return sessionManager.getTokenRemainingTime()
    }

    fun getUserId(): String? {
        return sessionManager.getUserId()
    }

    fun getUserEmail(): String? {
        return sessionManager.getUserEmail()
    }

    fun clearSession() {
        sessionManager.clearSession()
    }

    fun saveAuthToken(token: String) {
        sessionManager.saveAuthToken(token)
    }

    fun getAuthToken(): String? {
        return sessionManager.getAuthToken()
    }

    fun saveToken(token: String) {
        sessionManager.saveToken(token)
    }

    fun getToken(): String? {
        return sessionManager.getToken()
    }

    fun isFirstTimeUser(): Boolean {
        return sessionManager.isFirstTimeUser()
    }

    fun setFirstTimeDone() {
        sessionManager.setFirstTimeDone()
    }
} 