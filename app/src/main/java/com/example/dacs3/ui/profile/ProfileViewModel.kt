package com.example.dacs3.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: WorkspaceRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val uiState: StateFlow<ProfileUiState> = _uiState
    
    init {
        getUserProfile()
    }
    
    fun getUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            
            try {
                val userId = sessionManager.getUserId()
                
                if (userId != null) {
                    val user = repository.getUserById(userId)
                    if (user != null) {
                        _uiState.value = ProfileUiState.Success(user)
                    } else {
                        _uiState.value = ProfileUiState.Error("User not found")
                    }
                } else {
                    _uiState.value = ProfileUiState.Error("Not logged in")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // User can perform logout in ProfileScreen which calls AuthViewModel's logout
    // This is just to handle profile-specific logic
}

sealed class ProfileUiState {
    object Initial : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: com.example.dacs3.data.local.UserEntity) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
} 