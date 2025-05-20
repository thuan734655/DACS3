package com.example.dacs3.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Message
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.repository.impl.UserRepositoryImpl
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.data.websocket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel quản lý dữ liệu chat qua WebSocket
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val sessionManager: SessionManager,
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    // Trạng thái kết nối
    val isConnected = webSocketManager.isConnected
    
    // Tin nhắn
    val messages = webSocketManager.messages
    
    // Trạng thái đang nhập
    val typingStatus = webSocketManager.typingStatus
    
    // Người dùng online
    val onlineUsers = webSocketManager.onlineUsers
    
    // Các thông báo
    val notifications = webSocketManager.notifications
    
    // Trạng thái workspace đã tham gia
    val joinedWorkspaces = webSocketManager.joinedWorkspaces
    
    // Trạng thái channel đã tham gia
    val joinedChannels = webSocketManager.joinedChannels
    
    // Text đang nhập
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()
    
    // Trạng thái đang tải
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        // Kết nối tới WebSocket khi ViewModel được khởi tạo
        connect()
    }
    
    /**
     * Kết nối tới WebSocket server
     */
    fun connect() {
        viewModelScope.launch {
            sessionManager.getAuthToken()?.let { token ->
                webSocketManager.connect(token)
                sessionManager.getUserId()?.let { userId ->
                    webSocketManager.setUserId(userId)
                }
            }
        }
    }
    
    /**
     * Tham gia vào workspace
     */
    fun joinWorkspace(workspaceId: String) {
        webSocketManager.joinWorkspace(workspaceId)
    }
    
    /**
     * Tham gia vào channel
     */
    fun joinChannel(channelId: String) {
        webSocketManager.joinChannel(channelId)
    }
    
    /**
     * Thiết lập nội dung tin nhắn đang nhập
     */
    fun setMessageText(text: String) {
        _messageText.value = text
    }
    
    /**
     * Gửi tin nhắn trực tiếp
     */
    fun sendDirectMessage(receiverId: String) {
        val text = _messageText.value.trim()
        if (text.isNotEmpty()) {
            webSocketManager.sendDirectMessage(receiverId, text)
            _messageText.value = "" // Xóa text sau khi gửi
        }
    }
    
    /**
     * Gửi tin nhắn channel
     */
    fun sendChannelMessage(channelId: String) {
        val text = _messageText.value.trim()
        if (text.isNotEmpty()) {
            webSocketManager.sendChannelMessage(channelId, text)
            _messageText.value = "" // Xóa text sau khi gửi
        }
    }
    
    /**
     * Gửi trạng thái đang nhập trong channel
     */
    fun sendTypingIndicator(channelId: String, isTyping: Boolean) {
        webSocketManager.sendTypingIndicator(channelId, isTyping)
    }
    
    /**
     * Gửi trạng thái đang nhập trong tin nhắn trực tiếp
     */
    fun sendDirectTypingIndicator(receiverId: String, isTyping: Boolean) {
        webSocketManager.sendDirectTypingIndicator(receiverId, isTyping)
    }
    
    /**
     * Ngắt kết nối khi ViewModel bị hủy
     */
    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
