package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.entity.NotificationEntity
import com.example.dacs3.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications

    private val _workspaceNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val workspaceNotifications: StateFlow<List<NotificationEntity>> = _workspaceNotifications

    private val _unreadNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val unreadNotifications: StateFlow<List<NotificationEntity>> = _unreadNotifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private val PAGE_SIZE = 20

    fun fetchAllNotifications(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // First, get data from local database (offline-first approach)
                notificationRepository.getAll()
                    .catch { e ->
                        _error.value = e.message
                    }
                    .collectLatest { localNotifications ->
                        _notifications.value = localNotifications.sortedByDescending { it.created_at }
                        
                        // Get unread notifications
                        notificationRepository.getUnreadNotifications()
                            .catch { e ->
                                _error.value = e.message
                            }
                            .collectLatest { unread ->
                                _unreadNotifications.value = unread.sortedByDescending { it.created_at }
                            }
                        
                        // Then, if online or forced refresh, sync with remote
                        if (forceRefresh) {
                            syncWithRemote()
                        }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWorkspaceNotifications(workspaceId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // First, get data from local database (offline-first approach)
                notificationRepository.getNotificationsByWorkspaceId(workspaceId)
                    .catch { e ->
                        _error.value = e.message
                    }
                    .collectLatest { localNotifications ->
                        _workspaceNotifications.value = localNotifications.sortedByDescending { it.created_at }
                        
                        // Then, if online or forced refresh, sync with remote
                        if (forceRefresh) {
                            syncWorkspaceNotificationsWithRemote(workspaceId)
                        }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun syncWithRemote() {
        try {
            val response = notificationRepository.getAllNotificationsFromApi(
                page = _currentPage.value,
                limit = PAGE_SIZE
            )
            
            if (response.success) {
                _hasMoreData.value = response.data.size >= PAGE_SIZE
                
                // Also sync unread notifications
                val unreadResponse = notificationRepository.getUnreadNotificationsFromApi()
                if (!unreadResponse.success) {
                    _error.value = "Failed to sync unread notifications"
                }
            } else {
                _error.value = "Failed to sync notifications with server"
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    private suspend fun syncWorkspaceNotificationsWithRemote(workspaceId: String) {
        try {
            val response = notificationRepository.getAllNotificationsFromApi(
                page = _currentPage.value,
                limit = PAGE_SIZE,
                workspaceId = workspaceId
            )
            
            if (response.success) {
                _hasMoreData.value = response.data.size >= PAGE_SIZE
            } else {
                _error.value = "Failed to sync workspace notifications with server"
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun loadMoreNotifications() {
        if (!_isLoading.value && _hasMoreData.value) {
            _currentPage.value = _currentPage.value + 1
            viewModelScope.launch {
                syncWithRemote()
            }
        }
    }

    fun loadMoreWorkspaceNotifications(workspaceId: String) {
        if (!_isLoading.value && _hasMoreData.value) {
            _currentPage.value = _currentPage.value + 1
            viewModelScope.launch {
                syncWorkspaceNotificationsWithRemote(workspaceId)
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.markAsRead(notificationId)
                if (!response.success) {
                    _error.value = "Failed to mark notification as read"
                }
                // Refresh notifications to update UI
                fetchAllNotifications(true)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun markAllAsRead(workspaceId: String? = null) {
        viewModelScope.launch {
            try {
                val success = notificationRepository.markAllAsRead(workspaceId)
                if (!success) {
                    _error.value = "Failed to mark all notifications as read"
                }
                // Refresh notifications to update UI
                fetchAllNotifications(true)
                if (workspaceId != null) {
                    fetchWorkspaceNotifications(workspaceId, true)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun refreshNotifications() {
        _currentPage.value = 1
        fetchAllNotifications(true)
    }

    fun refreshWorkspaceNotifications(workspaceId: String) {
        _currentPage.value = 1
        fetchWorkspaceNotifications(workspaceId, true)
    }

    fun clearError() {
        _error.value = null
    }
} 