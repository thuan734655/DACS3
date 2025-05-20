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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import retrofit2.HttpException
import java.io.IOException
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
                // Always ensure status is lowercase to match API requirements
                val normalizedStatus = status?.lowercase()
                
                _state.update { it.copy(
                    isLoading = true,
                    error = null
                )}
                
                Log.d("InvitationsViewModel", "Loading invitations with status: $normalizedStatus")
                val response = invitationRepository.getInvitations(normalizedStatus)
                
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
            // Set state to show we're processing
            _state.update { it.copy(
                isAccepting = true,
                processingInvitationId = invitationId,
                actionError = null
            )}
            
            try {
                try {
                    Log.d("InvitationsViewModel", "Accepting invitation: $invitationId")
                    val response = invitationRepository.acceptInvitation(invitationId)
                    
                    if (response.success) {
                        Log.d("InvitationsViewModel", "Successfully accepted invitation")
                        _state.update { it.copy(
                            isAccepting = false,
                            processingInvitationId = null,
                            actionSuccess = true
                        )}
                        
                        // Reload invitations after successful action
                        loadInvitations(state.value.currentFilter)
                    } else {
                        Log.e("InvitationsViewModel", "Failed to accept invitation")
                        _state.update { it.copy(
                            isAccepting = false,
                            processingInvitationId = null,
                            actionError = "Unable to accept invitation"
                        )}
                    }
                } catch (e: retrofit2.HttpException) {
                    // Handle HTTP errors specifically
                    when (e.code()) {
                        500 -> {
                            Log.e("InvitationsViewModel", "Server error when accepting invitation", e)
                            // Show a more specific message for the 500 error
                            _state.update { it.copy(
                                isAccepting = false,
                                processingInvitationId = null,
                                actionError = "Server error processing invitation. We've been notified."
                            )}
                            
                            // For 500 errors with this specific message, still reload the UI
                            // This is because the server might have processed the request despite returning an error
                            try { loadInvitations(state.value.currentFilter) } catch (ex: Exception) { }
                        }
                        else -> {
                            Log.e("InvitationsViewModel", "HTTP error when accepting invitation: ${e.code()}", e)
                            _state.update { it.copy(
                                isAccepting = false,
                                processingInvitationId = null,
                                actionError = "Error accepting invitation (${e.code()})"
                            )}
                        }
                    }
                } catch (e: IOException) {
                    Log.e("InvitationsViewModel", "Network error while accepting invitation", e)
                    _state.update { it.copy(
                        isAccepting = false,
                        processingInvitationId = null,
                        actionError = "Network error. Please check your connection."
                    )}
                }
            } catch (e: Exception) {
                Log.e("InvitationsViewModel", "Error accepting invitation", e)
                _state.update { it.copy(
                    isAccepting = false,
                    processingInvitationId = null,
                    actionError = "Error: ${e.message ?: "Unknown error"}"
                )}
            }
        }
    }
    
    fun rejectInvitation(invitationId: String) {
        viewModelScope.launch {
            // Set state to show we're processing
            _state.update { it.copy(
                isRejecting = true,
                processingInvitationId = invitationId,
                actionError = null
            )}
            
            try {
                try {
                    val response = invitationRepository.rejectInvitation(invitationId)
                    if (response.success) {
                        _state.update { it.copy(
                            isRejecting = false,
                            processingInvitationId = null,
                            actionSuccess = true
                        )}
                        // Reload invitations to reflect changes
                        loadInvitations(state.value.currentFilter)
                    } else {
                        _state.update { it.copy(
                            isRejecting = false,
                            processingInvitationId = null,
                            actionError = "Unable to reject invitation"
                        )}
                    }
                } catch (e: retrofit2.HttpException) {
                    // Handle HTTP errors specifically
                    when (e.code()) {
                        500 -> {
                            Log.e("InvitationsViewModel", "Server error when rejecting invitation", e)
                            // Show a more specific message for the 500 error
                            _state.update { it.copy(
                                isRejecting = false,
                                processingInvitationId = null,
                                actionError = "Server error processing invitation. We've been notified."
                            )}
                            
                            // For 500 errors with this specific message, still reload the UI
                            // This is because the server might have processed the request despite returning an error
                            // (The socket.io emit is causing the 500 error but the status change succeeds)
                            try { loadInvitations(state.value.currentFilter) } catch (ex: Exception) { }
                        }
                        else -> {
                            Log.e("InvitationsViewModel", "HTTP error when rejecting invitation: ${e.code()}", e)
                            _state.update { it.copy(
                                isRejecting = false,
                                processingInvitationId = null,
                                actionError = "Error rejecting invitation (${e.code()})"
                            )}
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("InvitationsViewModel", "Failed to reject invitation", e)
                _state.update { it.copy(
                    isRejecting = false,
                    processingInvitationId = null,
                    actionError = "Error: ${e.message ?: "Unknown error"}"
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
    val isAccepting: Boolean = false,
    val isRejecting: Boolean = false,
    val invitations: List<Invitation> = emptyList(),
    val error: String? = null,
    val processingInvitationId: String? = null,
    val actionSuccess: Boolean = false,
    val actionError: String? = null,
    val currentFilter: String = "pending"
)
