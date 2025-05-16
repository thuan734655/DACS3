package com.example.dacs3.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.User
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isUpdateSuccessful: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val userId = sessionManager.getUserId()
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "User not logged in"
                        )
                    }
                    return@launch
                }
                
                val response = userRepository.getUserByIdFromApi(userId)
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            user = response.data
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load user profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun updateProfile(username: String, email: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    isUpdateSuccessful = false
                )
            }
            
            try {
                val userId = sessionManager.getUserId() ?: throw IllegalStateException("User not logged in")
                
                val response = userRepository.updateUser(
                    id = userId,
                    name = username,
                    avatar = uiState.value.user?.avatar
                )
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            user = response.data,
                            isUpdateSuccessful = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to update profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            // You might want to navigate back to login screen here
            // or use a callback to notify the UI
        }
    }
    
    fun updateAvatar(avatarUrl: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    isUpdateSuccessful = false
                )
            }
            
            try {
                val userId = sessionManager.getUserId() ?: throw IllegalStateException("User not logged in")
                
                val response = userRepository.updateUser(
                    id = userId,
                    name = uiState.value.user?.name,
                    avatar = avatarUrl
                )
                
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            user = response.data,
                            isUpdateSuccessful = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to update avatar"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
} 