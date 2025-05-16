package com.example.dacs3.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.repository.NotificationRepository
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    fun loadNotifications() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val userId = userManager.getCurrentUserId()
                if (userId != null) {
                    val response = notificationRepository.getUnreadNotificationsFromApi()
                    if (response.success) {
                        _uiState.update { it.copy(
                            notifications = response.data.sortedByDescending { notification -> notification.created_at },
                            isLoading = false
                        ) }
                    } else {
                        _uiState.update { it.copy(error = "Failed to load notifications", isLoading = false) }
                    }
                } else {
                    _uiState.update { it.copy(error = "User not authenticated", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred", isLoading = false) }
            }
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.markAsRead(notificationId)
                if (response.success) {
                    // Update the notification in the UI state
                    val updatedNotifications = _uiState.value.notifications.map { notification ->
                        if (notification._id == notificationId) {
                            notification.copy(is_read = true)
                        } else {
                            notification
                        }
                    }
                    _uiState.update { it.copy(notifications = updatedNotifications) }
                } else {
                    _uiState.update { it.copy(error = "Failed to mark notification as read") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred") }
            }
        }
    }

    fun markAllAsRead() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val result = notificationRepository.markAllAsRead()
                if (result) {
                    // Update all notifications as read in the UI state
                    val updatedNotifications = _uiState.value.notifications.map { notification ->
                        notification.copy(is_read = true)
                    }
                    _uiState.update { it.copy(notifications = updatedNotifications, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Failed to mark all notifications as read", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred", isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
