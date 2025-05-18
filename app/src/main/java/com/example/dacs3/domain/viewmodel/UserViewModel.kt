package com.example.dacs3.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.User
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null,
    val workspaceMembers: List<User> = emptyList(),
    val searchResults: List<User> = emptyList()
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    /**
     * Tải thông tin người dùng hiện tại
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val userId = userManager.getCurrentUserId()
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Người dùng chưa đăng nhập"
                        )
                    }
                    return@launch
                }
                
                val response = userRepository.getUserByIdFromApi(userId)
                
                if (response.success && response.data != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            currentUser = response.data
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Không thể tải thông tin người dùng"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Lỗi: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Tải danh sách thành viên trong workspace
     */
    fun loadWorkspaceMembers(workspaceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = workspaceRepository.getWorkspaceMembersFromApi(workspaceId)
                
                if (response.success) {
                    // Chuyển đổi WorkspaceMember thành User
                    val members = response.data?.map { member ->
                        User(
                            _id = member._id,
                            name = member.name,
                            avatar = member.avatar,
                            created_at = member.created_at
                        )
                    } ?: emptyList()
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workspaceMembers = members
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Không thể tải danh sách thành viên"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Lỗi: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Tìm kiếm người dùng theo tên
     */
    fun searchUsers(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = userRepository.searchUsersFromApi(query)
                
                if (response.success) {
                    // Cập nhật kết quả tìm kiếm vào state
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            searchResults = response.data ?: emptyList()
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Không thể tìm kiếm người dùng"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Lỗi: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Xóa thông báo lỗi
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}