package com.example.dacs3.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.UserEntity
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: WorkspaceRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun register(name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                // Create new user with random ID
                val newUser = UserEntity(
                    userId = UUID.randomUUID().toString(),
                    username = name,
                    avatarUrl = null,
                    isOnline = true
                )
                
                repository.insertUser(newUser)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(userId: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            try {
                val user = repository.getUserById(userId)
                if (user != null) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("User not found")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
} 