package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _unreadNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val unreadNotifications: StateFlow<List<Notification>> = _unreadNotifications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getAllNotifications(page: Int? = null, limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getAllNotifications(page, limit)
                if (response.isSuccessful && response.body()?.success == true) {
                    _notifications.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUnreadNotifications() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getUnreadNotifications()
                if (response.isSuccessful && response.body()?.success == true) {
                    _unreadNotifications.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.markAsRead(notificationId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh notifications lists after marking as read
                    refreshNotifications()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.markAllAsRead()
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh notifications lists after marking all as read
                    refreshNotifications()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteNotification(notificationId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh notifications lists after deletion
                    refreshNotifications()
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun registerFcmToken(token: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.registerFcmToken(token)
                if (!response.isSuccessful || response.body()?.success != true) {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun refreshNotifications() {
        getAllNotifications()
        getUnreadNotifications()
    }
} 