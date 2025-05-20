package com.example.dacs3.ui.workspace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.WorkspaceDetailData
import com.example.dacs3.data.repository.InvitationRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.websocket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class WorkspaceDetailState(
    val isLoading: Boolean = false,
    val data: WorkspaceDetailData? = null,
    val error: String? = null,
    val actionInProgress: Boolean = false,
    val actionSuccess: Boolean = false,
    val actionError: String? = null
)

@HiltViewModel
class WorkspaceDetailViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
    private val invitationRepository: InvitationRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _state = MutableStateFlow(WorkspaceDetailState())
    val state: StateFlow<WorkspaceDetailState> = _state.asStateFlow()
    
    // To store the current workspace ID
    private var currentWorkspaceId: String? = null
    
    init {
        // Subscribe to WebSocket notifications
        viewModelScope.launch {
            webSocketManager.notifications.collect { notifications ->
                // If a notification arrives and we have a workspace loaded, refresh the data
                currentWorkspaceId?.let { id ->
                    // Only reload if we're not currently loading
                    if (!_state.value.isLoading) {
                        loadWorkspaceDetails(id)
                    }
                }
            }
        }
    }

    fun loadWorkspaceDetails(workspaceId: String) {
        // Store the current workspace ID
        currentWorkspaceId = workspaceId
        
        // Join the workspace room via WebSocket
        webSocketManager.joinWorkspace(workspaceId)
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
                
                if (response.success && response.data != null) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            data = response.data
                        )
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load workspace details"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkspaceDetailViewModel", "Network error loading workspace details", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Network error. Please check your connection."
                    )
                }
            } catch (e: HttpException) {
                Log.e("WorkspaceDetailViewModel", "HTTP error loading workspace details: ${e.code()}", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error ${e.code()}: ${e.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("WorkspaceDetailViewModel", "Error loading workspace details", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error loading workspace details: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Gửi lời mời tham gia workspace
     * 
     * @param email Email của người dùng cần mời
     */
    fun sendInvitation(email: String) {
        viewModelScope.launch {
            try {
                // Update state to show action in progress
                _state.update { it.copy(
                    actionInProgress = true,
                    actionSuccess = false,
                    actionError = null
                )}
                
                val currentId = currentWorkspaceId
                if (currentId == null) {
                    _state.update { it.copy(
                        actionInProgress = false,
                        actionError = "Chưa chọn workspace"
                    )}
                    return@launch
                }
                
                // Gọi API để gửi lời mời
                val response = invitationRepository.sendWorkspaceInvitation(email, currentId)
                
                if (response.success) {
                    // Cập nhật trạng thái thành công
                    _state.update { it.copy(
                        actionInProgress = false,
                        actionSuccess = true
                    )}
                    
                    // Không cần reload workspace vì member chưa được thêm vào, 
                    // chỉ có invitation được tạo
                } else {
                    // Cập nhật trạng thái lỗi
                    _state.update { it.copy(
                        actionInProgress = false,
                        actionError = response.message ?: "Không thể gửi lời mời"
                    )}
                }
            } catch (e: IOException) {
                Log.e("WorkspaceDetailViewModel", "Network error sending invitation", e)
                _state.update { it.copy(
                    actionInProgress = false,
                    actionError = "Lỗi kết nối. Vui lòng kiểm tra kết nối mạng."
                )}
            } catch (e: HttpException) {
                Log.e("WorkspaceDetailViewModel", "HTTP error sending invitation: ${e.code()}", e)
                _state.update { it.copy(
                    actionInProgress = false,
                    actionError = "Lỗi ${e.code()}: ${e.message()}"
                )}
            } catch (e: Exception) {
                Log.e("WorkspaceDetailViewModel", "Error sending invitation", e)
                _state.update { it.copy(
                    actionInProgress = false,
                    actionError = "Lỗi gửi lời mời: ${e.message}"
                )}
            }
        }
    }
    
    /**
     * Reset action state after handling success/error
     */
    fun resetActionState() {
        _state.update { it.copy(
            actionInProgress = false,
            actionSuccess = false,
            actionError = null
        )}
    }
}
