package com.example.dacs3.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.HomeState
import com.example.dacs3.data.repository.HomeRepository
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()
    
    private var currentWorkspaceId: String? = null
    
    init {
        loadWorkspaces()
    }
    
    fun loadWorkspaces() {
        viewModelScope.launch {
            homeRepository.getWorkspaces().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val workspaces = result.data
                        if (workspaces != null && workspaces.isNotEmpty()) {
                            // Lưu danh sách workspace vào state
                            _homeState.update { currentState ->
                                currentState.copy(workspaces = workspaces)
                            }
                            
                            // Lấy workspace đầu tiên nếu chưa có workspace nào được chọn
                            if (currentWorkspaceId == null) {
                                val firstWorkspace = workspaces[0]
                                currentWorkspaceId = firstWorkspace.id
                                
                                Log.d("HomeViewModel", "Đang đặt workspace mặc định: ${firstWorkspace.name}")
                                
                                _homeState.update { currentState ->
                                    currentState.copy(currentWorkspace = firstWorkspace)
                                }
                                // Only load workspace data if ID is valid
                                val workspaceId = firstWorkspace.id
                                if (workspaceId != null && workspaceId.isNotBlank()) {
                                    loadWorkspaceData(workspaceId)
                                }
                            }
                        } else {
                            _homeState.update { currentState ->
                                currentState.copy(
                                    workspaces = emptyList(),
                                    channels = emptyList(),
                                    unreadChannels = emptyList(),
                                    notifications = emptyList(),
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _homeState.update { currentState ->
                            currentState.copy(error = result.message, isLoading = false)
                        }
                    }
                    is Resource.Loading -> {
                        _homeState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }
    
    // Phương thức mới để xử lý việc chọn workspace từ sidebar
    fun switchWorkspace(workspaceId: String?) {
        if (workspaceId == null || workspaceId.isBlank()) {
            _homeState.update { currentState ->
                currentState.copy(error = "Invalid workspace ID", isLoading = false)
            }
            return
        }
        
        Log.d("HomeViewModel", "Đang chuyển đổi sang workspace mới với ID: $workspaceId")
        
        // Reset toàn bộ state về mặc định, chỉ giữ lại danh sách workspace
        val currentWorkspaces = _homeState.value.workspaces
        _homeState.value = HomeState(
            workspaces = currentWorkspaces,
            isLoading = true
        )
        
        // Cập nhật ID workspace hiện tại
        currentWorkspaceId = workspaceId
        
        // Tải thông tin chi tiết workspace từ server
        viewModelScope.launch {
            homeRepository.getWorkspace(workspaceId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val workspace = result.data
                        if (workspace != null) {
                            Log.d("HomeViewModel", "Đã tải workspace từ server: ${workspace.name}")
                            
                            // Cập nhật workspace hiện tại
                            _homeState.update { currentState ->
                                currentState.copy(
                                    currentWorkspace = workspace
                                )
                            }
                            
                            // Tiếp tục tải channels và notifications cho workspace này
                            loadWorkspaceChannelsAndNotifications(workspaceId)
                        } else {
                            Log.d("HomeViewModel", "Server trả về workspace null")
                            
                            // Thử tìm trong danh sách workspaces đã có
                            val cachedWorkspace = _homeState.value.workspaces.find { it.id == workspaceId }
                            if (cachedWorkspace != null) {
                                Log.d("HomeViewModel", "Sử dụng workspace từ cache: ${cachedWorkspace.name}")
                                
                                _homeState.update { currentState ->
                                    currentState.copy(
                                        currentWorkspace = cachedWorkspace
                                    )
                                }
                                
                                // Tải channels và notifications cho workspace này
                                loadWorkspaceChannelsAndNotifications(workspaceId)
                            } else {
                                _homeState.update { currentState ->
                                    currentState.copy(
                                        error = "Workspace not found",
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "Lỗi khi tải workspace: ${result.message}")
                        
                        // Thử tìm trong danh sách workspaces đã có
                        val cachedWorkspace = _homeState.value.workspaces.find { it.id == workspaceId }
                        if (cachedWorkspace != null) {
                            Log.d("HomeViewModel", "Sử dụng workspace từ cache: ${cachedWorkspace.name}")
                            
                            _homeState.update { currentState ->
                                currentState.copy(
                                    currentWorkspace = cachedWorkspace
                                )
                            }
                                
                            // Tải channels và notifications cho workspace này
                            loadWorkspaceChannelsAndNotifications(workspaceId)
                        } else {
                            _homeState.update { currentState ->
                                currentState.copy(
                                    error = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Loading -> {
                        // State đã được thiết lập isLoading = true ở trên
                    }
                }
            }
        }
    }
    
    // Hàm mới để tải channels và notifications đồng thời
    private fun loadWorkspaceChannelsAndNotifications(workspaceId: String) {
        viewModelScope.launch {
            // Tải channels
            homeRepository.getChannels(workspaceId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val channels = result.data ?: emptyList()
                        Log.d("HomeViewModel", "Đã tải ${channels.size} channels cho workspace")
                        
                        // Phân loại kênh thành có unread và không unread
                        val unreadChannels = channels.filter { channel -> channel.hasUnread }
                        
                        _homeState.update { currentState -> 
                            currentState.copy(
                                channels = channels,
                                unreadChannels = unreadChannels
                            ) 
                        }
                        
                        // Kiểm tra nếu cả channels và notifications đã tải xong
                        checkLoadingComplete()
                    }
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "Lỗi khi tải channels: ${result.message}")
                        _homeState.update { currentState ->
                            currentState.copy(error = result.message)
                        }
                        
                        // Kiểm tra nếu cả channels và notifications đã tải xong
                        checkLoadingComplete()
                    }
                    is Resource.Loading -> {
                        // Không làm gì
                    }
                }
            }
        }
        
        viewModelScope.launch {
            // Tải notifications
            homeRepository.getNotifications(workspaceId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val notifications = result.data ?: emptyList()
                        Log.d("HomeViewModel", "Đã tải ${notifications.size} notifications cho workspace")
                        
                        _homeState.update { currentState -> 
                            currentState.copy(
                                notifications = notifications.sortedByDescending { it.createdAt }
                            ) 
                        }
                        
                        // Kiểm tra nếu cả channels và notifications đã tải xong
                        checkLoadingComplete()
                    }
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "Lỗi khi tải notifications: ${result.message}")
                        _homeState.update { currentState ->
                            currentState.copy(error = result.message)
                        }
                        
                        // Kiểm tra nếu cả channels và notifications đã tải xong
                        checkLoadingComplete()
                    }
                    is Resource.Loading -> {
                        // Không làm gì
                    }
                }
            }
        }
    }
    
    private fun checkLoadingComplete() {
        // Đánh dấu quá trình tải đã hoàn tất
        _homeState.update { currentState ->
            currentState.copy(isLoading = false)
        }
    }
    
    fun loadWorkspaceData(workspaceId: String?) {
        // Safely handle null workspaceId
        if (workspaceId == null || workspaceId.isBlank()) {
            _homeState.update { currentState ->
                currentState.copy(error = "Invalid workspace ID", isLoading = false)
            }
            return
        }
        
        Log.d("HomeViewModel", "Đang tải dữ liệu mới cho workspace ID: $workspaceId")
        
        currentWorkspaceId = workspaceId
        
        // Đánh dấu đang tải
        _homeState.update { currentState ->
            currentState.copy(isLoading = true)
        }
        
        // Tải lại toàn bộ danh sách workspace để lấy dữ liệu mới nhất
        viewModelScope.launch {
            // Gọi API lấy danh sách workspace
            homeRepository.getWorkspaces().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val workspaces = result.data
                        if (workspaces != null && workspaces.isNotEmpty()) {
                            Log.d("HomeViewModel", "Đã tải ${workspaces.size} workspaces từ server")
                            
                            // Lưu danh sách workspace vào state
                            _homeState.update { currentState ->
                                currentState.copy(workspaces = workspaces)
                            }
                            
                            // Tìm workspace được chọn từ danh sách mới
                            val selectedWorkspace = workspaces.find { workspace -> 
                                workspace.id == workspaceId 
                            }
                            
                            if (selectedWorkspace != null) {
                                Log.d("HomeViewModel", "Đã tìm thấy workspace được chọn: ${selectedWorkspace.name}")
                                
                                // Cập nhật current workspace
                                _homeState.update { currentState ->
                                    currentState.copy(
                                        currentWorkspace = selectedWorkspace,
                                        isLoading = false
                                    )
                                }
                                
                                // Tiếp tục tải channels và notifications
                                loadChannels(workspaceId)
                                loadNotifications(workspaceId)
                            } else {
                                Log.d("HomeViewModel", "Không tìm thấy workspace ID: $workspaceId trong danh sách mới")
                                
                                // Gọi API để lấy thông tin chi tiết workspace
                                loadWorkspaceDetails(workspaceId)
                            }
                        } else {
                            Log.d("HomeViewModel", "Server trả về danh sách workspace rỗng")
                            
                            // Gọi API để lấy thông tin chi tiết workspace
                            loadWorkspaceDetails(workspaceId)
                        }
                    }
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "Lỗi khi tải danh sách workspace: ${result.message}")
                        
                        // Gọi API để lấy thông tin chi tiết workspace
                        loadWorkspaceDetails(workspaceId)
                    }
                    is Resource.Loading -> {
                        // Do nothing while loading
                    }
                }
            }
        }
    }
    
    // Hàm riêng để tải thông tin chi tiết của workspace
    private fun loadWorkspaceDetails(workspaceId: String) {
        viewModelScope.launch {
            // Gọi API lấy thông tin chi tiết workspace
            homeRepository.getWorkspace(workspaceId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val workspace = result.data
                        if (workspace != null) {
                            Log.d("HomeViewModel", "Đã lấy chi tiết workspace từ server: ${workspace.name}")
                            
                            _homeState.update { currentState ->
                                currentState.copy(
                                    currentWorkspace = workspace,
                                    isLoading = false
                                )
                            }
                            
                            // Tiếp tục tải channels và notifications
                            loadChannels(workspaceId)
                            loadNotifications(workspaceId)
                        } else {
                            Log.d("HomeViewModel", "Server trả về chi tiết workspace là null")
                            
                            _homeState.update { currentState ->
                                currentState.copy(
                                    error = "Cannot load workspace details",
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "Lỗi khi lấy chi tiết workspace: ${result.message}")
                        
                        // Fallback: Tìm trong danh sách workspaces đã có
                        val cachedWorkspace = _homeState.value.workspaces.find { it.id == workspaceId }
                        if (cachedWorkspace != null) {
                            Log.d("HomeViewModel", "Sử dụng workspace từ cache: ${cachedWorkspace.name}")
                            
                            _homeState.update { currentState ->
                                currentState.copy(
                                    currentWorkspace = cachedWorkspace,
                                    error = result.message,
                                    isLoading = false
                                )
                            }
                            
                            // Tiếp tục tải channels và notifications
                            loadChannels(workspaceId)
                            loadNotifications(workspaceId)
                        } else {
                            _homeState.update { currentState ->
                                currentState.copy(
                                    error = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Loading -> {
                        // Do nothing while loading
                    }
                }
            }
        }
    }
    
    private fun loadChannels(workspaceId: String) {
        if (workspaceId.isBlank()) return
        
        viewModelScope.launch {
            homeRepository.getChannels(workspaceId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val channels = result.data ?: emptyList()
                        // Phân loại kênh thành có unread và không unread
                        val unreadChannels = channels.filter { channel -> channel.hasUnread }
                        
                        _homeState.update { currentState -> 
                            currentState.copy(
                                channels = channels,
                                unreadChannels = unreadChannels,
                                isLoading = false
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        _homeState.update { currentState ->
                            currentState.copy(error = result.message, isLoading = false)
                        }
                    }
                    is Resource.Loading -> {
                        _homeState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }
    
    private fun loadNotifications(workspaceId: String) {
        if (workspaceId.isBlank()) return
        
        viewModelScope.launch {
            homeRepository.getNotifications(workspaceId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val notifications = result.data ?: emptyList()
                        _homeState.update { currentState -> 
                            currentState.copy(
                                notifications = notifications.sortedByDescending { notification -> notification.createdAt },
                                isLoading = false
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        _homeState.update { currentState ->
                            currentState.copy(error = result.message, isLoading = false)
                        }
                    }
                    is Resource.Loading -> {
                        _homeState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }
    
    fun refreshData() {
        currentWorkspaceId?.let { workspaceId -> loadWorkspaceData(workspaceId) }
    }
} 