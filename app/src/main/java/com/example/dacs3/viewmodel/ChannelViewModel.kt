package com.example.dacs3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {

    // StateFlow cho các kênh
    private val _allChannels = MutableStateFlow<List<Channel>>(emptyList())
    val allChannels: StateFlow<List<Channel>> = _allChannels.asStateFlow()

    // StateFlow cho kênh đang chọn
    private val _selectedChannel = MutableStateFlow<Channel?>(null)
    val selectedChannel: StateFlow<Channel?> = _selectedChannel.asStateFlow()

    // StateFlow cho các kênh chưa đọc (sẽ được phân tích từ lastMessageAt và lastMessageId)
    private val _unreadChannels = MutableStateFlow<List<Channel>>(emptyList())
    val unreadChannels: StateFlow<List<Channel>> = _unreadChannels.asStateFlow()

    // StateFlow cho trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow cho lỗi
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // StateFlow cho trạng thái hiển thị/ẩn các section
    private val _channelSectionExpanded = MutableStateFlow(true)
    val channelSectionExpanded: StateFlow<Boolean> = _channelSectionExpanded.asStateFlow()

    private val _activitySectionExpanded = MutableStateFlow(true)
    val activitySectionExpanded: StateFlow<Boolean> = _activitySectionExpanded.asStateFlow()

    init {
        loadAllChannels()
    }

    // Lấy tất cả kênh
    fun loadAllChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = channelRepository.getAllChannels()
                if (response.isSuccessful && response.body()?.success == true) {
                    val channels = response.body()?.data ?: emptyList()
                    _allChannels.value = channels
                    
                    // Giả định unread channels nếu lastMessageAt > lastViewedAt (sẽ cần thêm logic)
                    updateUnreadChannels()
                } else {
                    _error.value = response.body()?.message ?: "Failed to load channels"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error loading channels", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Lấy channel theo ID
    fun getChannelById(channelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = channelRepository.getChannelById(channelId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _selectedChannel.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Failed to load channel"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error loading channel", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Tạo channel mới
    fun createChannel(name: String, description: String, workspaceId: String, isPrivate: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val newChannel = Channel(
                    name = name,
                    description = description,
                    workspaceId = workspaceId,
                    isPrivate = isPrivate,
                    createdBy = "current_user_id", // Giả định ID của người dùng hiện tại
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                val response = channelRepository.createChannel(newChannel)
                if (response.isSuccessful && response.body()?.success == true) {
                    val createdChannel = response.body()?.data
                    if (createdChannel != null) {
                        // Thêm kênh mới vào danh sách
                        _allChannels.update { currentList -> 
                            currentList + createdChannel 
                        }
                        _selectedChannel.value = createdChannel
                    }
                } else {
                    _error.value = response.body()?.message ?: "Failed to create channel"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error creating channel", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Cập nhật channel
    fun updateChannel(channelId: String, name: String, description: String, isPrivate: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Lấy channel hiện tại từ danh sách
                val currentChannel = _allChannels.value.find { it.id == channelId } 
                if (currentChannel != null) {
                    // Cập nhật thông tin channel
                    val updatedChannel = currentChannel.copy(
                        name = name,
                        description = description,
                        isPrivate = isPrivate,
                        updatedAt = Date()
                    )
                    
                    val response = channelRepository.updateChannel(channelId, updatedChannel)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val responseChannel = response.body()?.data
                        if (responseChannel != null) {
                            // Cập nhật danh sách channels
                            _allChannels.update { currentList ->
                                currentList.map { 
                                    if (it.id == channelId) responseChannel else it 
                                }
                            }
                            
                            // Cập nhật channel đang chọn nếu đang được chọn
                            if (_selectedChannel.value?.id == channelId) {
                                _selectedChannel.value = responseChannel
                            }
                        }
                    } else {
                        _error.value = response.body()?.message ?: "Failed to update channel"
                    }
                } else {
                    _error.value = "Channel not found"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error updating channel", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Xóa channel
    fun deleteChannel(channelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = channelRepository.deleteChannel(channelId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Xóa channel khỏi danh sách
                    _allChannels.update { currentList ->
                        currentList.filter { it.id != channelId }
                    }
                    
                    // Nếu channel đang chọn bị xóa, reset selected channel
                    if (_selectedChannel.value?.id == channelId) {
                        _selectedChannel.value = null
                    }
                    
                    // Cập nhật danh sách unread channels
                    updateUnreadChannels()
                } else {
                    _error.value = response.body()?.message ?: "Failed to delete channel"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error deleting channel", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Thêm thành viên vào channel
    fun addMemberToChannel(channelId: String, memberId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = channelRepository.addMember(channelId, memberId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedChannel = response.body()?.data
                    if (updatedChannel != null) {
                        // Cập nhật channel trong danh sách
                        _allChannels.update { currentList ->
                            currentList.map { 
                                if (it.id == channelId) updatedChannel else it 
                            }
                        }
                        
                        // Cập nhật selected channel nếu cần
                        if (_selectedChannel.value?.id == channelId) {
                            _selectedChannel.value = updatedChannel
                        }
                    }
                } else {
                    _error.value = response.body()?.message ?: "Failed to add member"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error adding member", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Xóa thành viên khỏi channel
    fun removeMemberFromChannel(channelId: String, memberId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = channelRepository.removeMember(channelId, memberId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedChannel = response.body()?.data
                    if (updatedChannel != null) {
                        // Cập nhật channel trong danh sách
                        _allChannels.update { currentList ->
                            currentList.map { 
                                if (it.id == channelId) updatedChannel else it 
                            }
                        }
                        
                        // Cập nhật selected channel nếu cần
                        if (_selectedChannel.value?.id == channelId) {
                            _selectedChannel.value = updatedChannel
                        }
                    }
                } else {
                    _error.value = response.body()?.message ?: "Failed to remove member"
                }
            } catch (e: Exception) {
                Log.e("ChannelViewModel", "Error removing member", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Cập nhật danh sách kênh chưa đọc
    private fun updateUnreadChannels() {
        // Giả định logic tạm thời - trong thực tế sẽ cần so sánh lastMessageAt với lastViewedAt
        val unread = _allChannels.value.filter { channel ->
            // Giả định là nếu có lastMessageId mà chưa được đọc
            channel.lastMessageId != null && channel.lastMessageAt != null
        }
        _unreadChannels.value = unread
    }

    // Chọn channel
    fun selectChannel(channel: Channel) {
        _selectedChannel.value = channel
    }

    // Lấy channels cho workspace cụ thể
    fun getChannelsForWorkspace(workspaceId: String): List<Channel> {
        return _allChannels.value.filter { it.workspaceId == workspaceId }
    }

    // Lấy unread channels cho workspace cụ thể
    fun getUnreadChannelsForWorkspace(workspaceId: String): List<Channel> {
        return _unreadChannels.value.filter { it.workspaceId == workspaceId }
    }

    // Toggle trạng thái hiển thị/ẩn sections
    fun toggleChannelSection() {
        _channelSectionExpanded.update { !it }
    }

    fun toggleActivitySection() {
        _activitySectionExpanded.update { !it }
    }

    // Clear error 
    fun clearError() {
        _error.value = null
    }
}
