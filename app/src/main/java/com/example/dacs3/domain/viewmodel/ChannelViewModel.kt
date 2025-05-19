package com.example.dacs3.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.entity.ChannelEntity
import com.example.dacs3.data.local.entity.WorkspaceEntity
import com.example.dacs3.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _channels = MutableStateFlow<List<ChannelEntity>>(emptyList())
    val channels: StateFlow<List<ChannelEntity>> = _channels

    private val _selectedChannel = MutableStateFlow<ChannelEntity?>(null)
    val selectedChannel: StateFlow<ChannelEntity?> = _selectedChannel

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private val PAGE_SIZE = 20

    fun fetchChannelsForWorkspace(workspace: WorkspaceEntity, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // First, get data from local database (offline-first approach)
                channelRepository.getChannelsByWorkspaceId(workspace._id)
                    .catch { e -> _error.value = e.message }
                    .collectLatest { local ->
                        _channels.value = local
                        if (_selectedChannel.value == null && local.isNotEmpty()) {
                            _selectedChannel.value = local.first()
                        }
                        if (forceRefresh) {
                            syncWithRemote(workspace._id)
                        }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun syncWithRemote(workspaceId: String) {
        try {
            val response = channelRepository.getChannelsByWorkspaceFromApi(
                page = _currentPage.value,
                limit = PAGE_SIZE,
                workspaceId = workspaceId
            )

            if (response.success) {
                val list = response.data ?: emptyList()
                _hasMoreData.value = list.size >= PAGE_SIZE
            }
            else {
                _error.value = "Failed to sync channels with server"
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun loadMoreChannels(workspaceId: String) {
        if (!_isLoading.value && _hasMoreData.value) {
            _currentPage.value = _currentPage.value + 1
            viewModelScope.launch {
                syncWithRemote(workspaceId)
            }
        }
    }

    fun selectChannel(channel: ChannelEntity) {
        _selectedChannel.value = channel
    }

    fun createChannel(
        name: String, 
        description: String?, 
        workspaceId: String, 
        createdBy: String, 
        isPrivate: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = channelRepository.createChannel(
                    name = name,
                    description = description,
                    workspaceId = workspaceId,
                    createdBy = createdBy,
                    isPrivate = isPrivate
                )
                
                if (response.success && response.data != null) {
                    // Force refresh to get the new channel
                    refreshChannels(workspaceId)

                } else {
                    _error.value = "Failed to create channel"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshChannels(workspaceId: String) {
        _currentPage.value = 1
        viewModelScope.launch {
            syncWithRemote(workspaceId)
        }
    }

    fun clearError() {
        _error.value = null
    }
} 