package com.example.dacs3.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.UserEntity
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: WorkspaceRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId
    
    init {
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            val userId = sessionManager.getUserId()
            _currentUserId.value = userId
            _authState.value = AuthState.Success
            
            // Set user online status
            userId?.let { id ->
                viewModelScope.launch {
                    try {
                        val user = repository.getUserById(id)
                        user?.let {
                            val updatedUser = it.copy(isOnline = true)
                            repository.updateUser(updatedUser)
                        }
                    } catch (e: Exception) {
                        // Ignore errors during auto-login
                    }
                }
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                // Check if email already exists
                val users = repository.getAllUsers().first()
                val emailExists = users.any { it.email == email }
                
                if (emailExists) {
                    _authState.value = AuthState.Error("Email already exists")
                    return@launch
                }
                
                // Create new user with random ID
                val newUser = UserEntity(
                    userId = UUID.randomUUID().toString(),
                    username = username,
                    email = email,
                    password = password,
                    avatarUrl = null,
                    isOnline = true
                )
                
                repository.insertUser(newUser)
                
                // After registration, don't save session or set current user ID
                // Just show success so user can go to login screen
                
                _authState.value = AuthState.RegisterSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            try {
                val users = repository.getAllUsers().first()
                val user = users.find { it.email == email && it.password == password }
                
                if (user != null) {
                    _currentUserId.value = user.userId
                    
                    // Update user to online status
                    val updatedUser = user.copy(isOnline = true)
                    repository.updateUser(updatedUser)
                    
                    // Save session
                    sessionManager.saveUserSession(user.userId, email)
                    
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                try {
                    val user = repository.getUserById(userId)
                    user?.let {
                        // Update user to offline status
                        val updatedUser = it.copy(isOnline = false)
                        repository.updateUser(updatedUser)
                    }
                } catch (e: Exception) {
                    // Ignore errors during logout
                }
            }
            
            // Clear session
            sessionManager.clearSession()
            
            _currentUserId.value = null
            _authState.value = AuthState.Initial
        }
    }
    
    // Check if user is already logged in
    fun checkLoggedInStatus(): Boolean {
        return sessionManager.isLoggedIn()
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
    object RegisterSuccess : AuthState()
} 