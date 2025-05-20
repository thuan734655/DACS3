package com.example.dacs3.data.websocket

import android.util.Log
import com.example.dacs3.data.model.Message
import com.example.dacs3.data.model.Notification
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocketManager quản lý kết nối Socket.IO với server
 * Cho phép ứng dụng gửi và nhận thông tin thời gian thực như:
 * - Tin nhắn
 * - Thông báo
 * - Trạng thái người dùng
 * - Chỉ báo đang nhập
 */
@Singleton
class WebSocketManager @Inject constructor(private val gson: Gson) {
    companion object {
        private const val TAG = "WebSocketManager"
        private const val SERVER_URL = "http://10.0.2.2:3000"
    }
    
    private var socket: Socket? = null
    private var userId: String? = null
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    // StateFlow cho tin nhắn trực tiếp
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    // StateFlow cho chỉ báo đang nhập
    data class TypingStatus(val userId: String, val channelId: String?, val isTyping: Boolean)
    private val _typingStatus = MutableStateFlow<TypingStatus?>(null)
    val typingStatus: StateFlow<TypingStatus?> = _typingStatus.asStateFlow()
    
    // StateFlow cho người dùng trực tuyến
    private val _onlineUsers = MutableStateFlow<List<String>>(emptyList())
    val onlineUsers: StateFlow<List<String>> = _onlineUsers.asStateFlow()
    
    // StateFlow cho thông báo
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    
    // StateFlow cho workspace đã tham gia
    private val _joinedWorkspaces = MutableStateFlow<Set<String>>(emptySet())
    val joinedWorkspaces: StateFlow<Set<String>> = _joinedWorkspaces.asStateFlow()
    
    // StateFlow cho channel đã tham gia
    private val _joinedChannels = MutableStateFlow<Set<String>>(emptySet())
    val joinedChannels: StateFlow<Set<String>> = _joinedChannels.asStateFlow()
    
    /**
     * Kết nối đến Socket.IO server với token xác thực
     */
    fun connect(token: String) {
        try {
            // Cấu hình options để kết nối Socket.IO với token xác thực
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = 10
                reconnectionDelay = 1000
                timeout = 10000
                
                // Thêm token xác thực qua query parameter
                query = "token=$token"
            }
            
            // Kết nối đến Server Socket.IO
            socket = IO.socket(SERVER_URL, options).apply {
                // Thiết lập event listeners
                setupEventListeners()
                
                // Kết nối
                connect()
            }
            
            Log.d(TAG, "Đang kết nối đến WebSocket server: $SERVER_URL")
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Lỗi kết nối đến WebSocket server", e)
        }
    }
    
    /**
     * Đặt ID người dùng hiện tại
     */
    fun setUserId(id: String) {
        userId = id
    }
    
    /**
     * Thiết lập các event listeners cho Socket.IO
     */
    private fun setupEventListeners() {
        socket?.let { socket ->
            // Các sự kiện kết nối
            socket.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Đã kết nối đến WebSocket server")
                _isConnected.value = true
            }
            
            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "Đã ngắt kết nối khỏi WebSocket server")
                _isConnected.value = false
                
                // Reset trạng thái khi ngắt kết nối
                _joinedWorkspaces.value = emptySet()
                _joinedChannels.value = emptySet()
            }
            
            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Lỗi kết nối: ${args.firstOrNull()}")
                _isConnected.value = false
            }
            
            // Sự kiện lỗi từ server
            socket.on("error") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as? JSONObject
                    val message = data?.optString("message") ?: "Unknown error"
                    Log.e(TAG, "Server error: $message")
                }
            }
            
            // Sự kiện tham gia workspace thành công
            socket.on("joined:workspace") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as? JSONObject
                    val workspaceId = data?.optString("workspaceId")
                    if (workspaceId != null) {
                        val currentWorkspaces = _joinedWorkspaces.value.toMutableSet()
                        currentWorkspaces.add(workspaceId)
                        _joinedWorkspaces.value = currentWorkspaces
                        Log.d(TAG, "Đã tham gia workspace: $workspaceId")
                    }
                }
            }
            
            // Sự kiện tham gia channel thành công
            socket.on("joined:channel") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as? JSONObject
                    val channelId = data?.optString("channelId")
                    if (channelId != null) {
                        val currentChannels = _joinedChannels.value.toMutableSet()
                        currentChannels.add(channelId)
                        _joinedChannels.value = currentChannels
                        Log.d(TAG, "Đã tham gia channel: $channelId")
                    }
                }
            }
            
            // Sự kiện tin nhắn trong channel
            socket.on("channel:message") { args ->
                try {
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val message = gson.fromJson(data.toString(), Message::class.java)
                        val currentMessages = _messages.value.toMutableList()
                        currentMessages.add(message)
                        _messages.value = currentMessages
                        Log.d(TAG, "Nhận tin nhắn channel: ${data}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi xử lý tin nhắn channel", e)
                }
            }
            
            // Sự kiện tin nhắn trực tiếp
            socket.on("direct:message") { args ->
                try {
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val message = gson.fromJson(data.toString(), Message::class.java)
                        val currentMessages = _messages.value.toMutableList()
                        currentMessages.add(message)
                        _messages.value = currentMessages
                        Log.d(TAG, "Nhận tin nhắn trực tiếp: ${data}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi xử lý tin nhắn trực tiếp", e)
                }
            }
            
            // Sự kiện chỉ báo đang nhập
            socket.on("user:typing") { args ->
                try {
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val userId = data.optString("userId")
                        val channelId = data.optString("channelId", null)
                        val isTyping = data.optBoolean("isTyping", false)
                        
                        _typingStatus.value = TypingStatus(userId, channelId, isTyping)
                        Log.d(TAG, "Trạng thái nhập: $userId đang nhập: $isTyping")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi xử lý chỉ báo đang nhập", e)
                }
            }
            
            // Sự kiện người dùng offline
            socket.on("user:offline") { args ->
                try {
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val userId = data.optString("userId")
                        
                        val users = _onlineUsers.value.toMutableList()
                        users.remove(userId)
                        _onlineUsers.value = users
                        
                        Log.d(TAG, "Người dùng offline: $userId")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi xử lý trạng thái offline", e)
                }
            }
            
            // Sự kiện trạng thái người dùng thay đổi
            socket.on("user:statusChanged") { args ->
                try {
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val userId = data.optString("userId")
                        val status = data.optString("status")
                        
                        if (status == "online" && !_onlineUsers.value.contains(userId)) {
                            val users = _onlineUsers.value.toMutableList()
                            users.add(userId)
                            _onlineUsers.value = users
                        }
                        
                        Log.d(TAG, "Trạng thái người dùng thay đổi: $userId là $status")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi xử lý thay đổi trạng thái", e)
                }
            }
            
            // Sự kiện thông báo mới
            socket.on("notification:new") { args ->
                try {
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val notification = gson.fromJson(data.toString(), Notification::class.java)
                        
                        val currentNotifications = _notifications.value.toMutableList()
                        currentNotifications.add(notification)
                        _notifications.value = currentNotifications
                        
                        Log.d(TAG, "Nhận thông báo mới: ${data}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi xử lý thông báo", e)
                }
            }
        }
    }

    /**
     * Gửi tin nhắn trực tiếp
     */
    fun sendDirectMessage(receiverId: String, content: String) {
        if (socket != null && socket?.connected() == true && userId != null) {
            try {
                val messageData = JSONObject().apply {
                    put("senderId", userId)
                    put("receiverId", receiverId)
                    put("content", content)
                    put("timestamp", System.currentTimeMillis())
                }
                
                socket?.emit("direct:message", messageData)
                
                // Lưu tin nhắn vào danh sách cục bộ
                val message = Message(
                    id = null, // Server sẽ gán ID
                    userId = userId,
                    content = content,
                    timestamp = System.currentTimeMillis(),
                    receiverId = receiverId,
                    type = "direct"
                )
                
                val currentMessages = _messages.value.toMutableList()
                currentMessages.add(message)
                _messages.value = currentMessages
                
                Log.d(TAG, "Đã gửi tin nhắn trực tiếp đến $receiverId")
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi gửi tin nhắn trực tiếp", e)
            }
        } else {
            Log.e(TAG, "Không thể gửi tin nhắn: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Gửi tin nhắn channel
     */
    fun sendChannelMessage(channelId: String, content: String) {
        if (socket != null && socket?.connected() == true && userId != null) {
            try {
                val messageData = JSONObject().apply {
                    put("userId", userId)
                    put("channelId", channelId)
                    put("content", content)
                    put("timestamp", System.currentTimeMillis())
                }
                
                socket?.emit("channel:message", messageData)
                
                Log.d(TAG, "Đã gửi tin nhắn channel đến $channelId")
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi gửi tin nhắn channel", e)
            }
        } else {
            Log.e(TAG, "Không thể gửi tin nhắn: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Gửi chỉ báo đang nhập cho channel
     */
    fun sendTypingIndicator(channelId: String, isTyping: Boolean) {
        if (socket != null && socket?.connected() == true) {
            try {
                val typingData = JSONObject().apply {
                    put("channelId", channelId)
                    put("isTyping", isTyping)
                }
                
                socket?.emit("typing:channel", typingData)
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi gửi chỉ báo đang nhập", e)
            }
        } else {
            Log.e(TAG, "Không thể gửi chỉ báo đang nhập: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Gửi chỉ báo đang nhập cho tin nhắn trực tiếp
     */
    fun sendDirectTypingIndicator(receiverId: String, isTyping: Boolean) {
        if (socket != null && socket?.connected() == true) {
            try {
                val typingData = JSONObject().apply {
                    put("receiverId", receiverId)
                    put("isTyping", isTyping)
                }
                
                socket?.emit("typing:direct", typingData)
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi gửi chỉ báo đang nhập trực tiếp", e)
            }
        } else {
            Log.e(TAG, "Không thể gửi chỉ báo đang nhập: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Tham gia một workspace
     */
    fun joinWorkspace(workspaceId: String) {
        if (socket != null && socket?.connected() == true) {
            socket?.emit("join:workspace", workspaceId)
            Log.d(TAG, "Đang tham gia workspace: $workspaceId")
        } else {
            Log.e(TAG, "Không thể tham gia workspace: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Tham gia một channel
     */
    fun joinChannel(channelId: String) {
        if (socket != null && socket?.connected() == true) {
            socket?.emit("join:channel", channelId)
            Log.d(TAG, "Đang tham gia channel: $channelId")
        } else {
            Log.e(TAG, "Không thể tham gia channel: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Tham gia một thread
     */
    fun joinThread(threadParentId: String) {
        if (socket != null && socket?.connected() == true) {
            socket?.emit("join:thread", threadParentId)
            Log.d(TAG, "Đang tham gia thread: $threadParentId")
        } else {
            Log.e(TAG, "Không thể tham gia thread: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Cập nhật trạng thái người dùng
     */
    fun updateStatus(status: String) {
        if (socket != null && socket?.connected() == true) {
            try {
                val statusData = JSONObject().apply {
                    put("status", status)
                }
                
                socket?.emit("user:status", statusData)
                Log.d(TAG, "Đã cập nhật trạng thái thành: $status")
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi cập nhật trạng thái", e)
            }
        } else {
            Log.e(TAG, "Không thể cập nhật trạng thái: socket null hoặc mất kết nối")
        }
    }
    
    /**
     * Ngắt kết nối khỏi server
     */
    fun disconnect() {
        socket?.disconnect()
        Log.d(TAG, "Đã ngắt kết nối khỏi WebSocket server")
    }
}
