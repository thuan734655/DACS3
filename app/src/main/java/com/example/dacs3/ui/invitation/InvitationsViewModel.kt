package com.example.dacs3.ui.invitation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Invitation
import com.example.dacs3.data.repository.InvitationRepository
import com.example.dacs3.data.repository.impl.InvitationRepositoryImpl
import com.example.dacs3.data.websocket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class InvitationsViewModel @Inject constructor(
    private val invitationRepository: InvitationRepositoryImpl,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _state = MutableStateFlow(InvitationsUiState())
    val state: StateFlow<InvitationsUiState> = _state.asStateFlow()

    private var notificationJob: Job? = null
    
    init {
        subscribeToNotifications()

        loadInvitations("pending")
    }
    
    private fun subscribeToNotifications() {
        notificationJob = viewModelScope.launch {
            webSocketManager.notifications.collectLatest { notificationList ->
                // Kiểm tra thông báo cuối cùng trong danh sách nếu có
                notificationList.lastOrNull()?.let { lastNotification ->
                    if (lastNotification.type == "INVITATION_CREATED" || 
                        lastNotification.type == "INVITATION_ACCEPTED" || 
                        lastNotification.type == "INVITATION_REJECTED") {
                        loadInvitations()
                    }
                }
            }
        }
    }
    
    fun loadInvitations(status: String? = null) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(
                    isLoading = true,
                    error = null
                )}
                
                val response = invitationRepository.getInvitations(status)
                
                if (response.success) {
                    _state.update { it.copy(
                        isLoading = false,
                        invitations = response.data
                    )}
                } else {
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Không thể tải lời mời"
                    )}
                }
            } catch (e: IOException) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Lỗi kết nối. Vui lòng kiểm tra kết nối mạng."
                )}
            } catch (e: HttpException) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Lỗi ${e.code()}: ${e.message()}"
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Lỗi: ${e.message}"
                )}
            }
        }
    }
    
    fun acceptInvitation(invitationId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(
                    processingInvitationId = invitationId,
                    actionSuccess = false,
                    actionError = null
                )}
                
                val response = invitationRepository.acceptInvitation(invitationId)
                
                if (response.success) {
                    _state.update { it.copy(
                        processingInvitationId = null,
                        actionSuccess = true
                    )}
                    
                    // Reload invitations after successful action
                    loadInvitations()
                } else {
                    _state.update { it.copy(
                        processingInvitationId = null,
                        actionError = "Không thể chấp nhận lời mời"
                    )}
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    processingInvitationId = null,
                    actionError = "Lỗi: ${e.message}"
                )}
            }
        }
    }
    
    fun rejectInvitation(invitationId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(
                    processingInvitationId = invitationId,
                    actionSuccess = false,
                    actionError = null
                )}
                
                val response = invitationRepository.rejectInvitation(invitationId)
                
                if (response.success) {
                    _state.update { it.copy(
                        processingInvitationId = null,
                        actionSuccess = true
                    )}
                    
                    // Reload invitations after successful action
                    loadInvitations()
                } else {
                    _state.update { it.copy(
                        processingInvitationId = null,
                        actionError = "Không thể từ chối lời mời"
                    )}
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    processingInvitationId = null,
                    actionError = "Lỗi: ${e.message}"
                )}
            }
        }
    }
    
    fun resetActionState() {
        _state.update { it.copy(
            actionSuccess = false,
            actionError = null
        )}
    }
    
    override fun onCleared() {
        super.onCleared()
        notificationJob?.cancel()
    }
}

data class InvitationsUiState(
    val isLoading: Boolean = false,
    val invitations: List<Invitation> = emptyList(),
    val error: String? = null,
    val processingInvitationId: String? = null,
    val actionSuccess: Boolean = false,
    val actionError: String? = null
)
