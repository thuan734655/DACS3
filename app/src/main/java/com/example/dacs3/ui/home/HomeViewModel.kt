package com.example.dacs3.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.WorkspaceMember
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.repository.ChannelRepository
import com.example.dacs3.data.repository.NotificationRepository
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val workspace: Workspace = Workspace(
        _id = "",
        name = "",
        description = "",
        created_by = User(
            _id = "",
            name = "",
            avatar = null,
            created_at = Date()
        ),
        members = emptyList(),
        channels = emptyList(),
        created_at = Date(),
    ),
    val channels: List<Channel> = emptyList(),
    val unreadChannels: List<Channel> = emptyList(),
    val notification: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val channelRepository: ChannelRepository,
    private val notificationRepository: NotificationRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val user = uiState.map { it.user }
    val workspace = uiState.map { it.workspace }
    val channels = uiState.map { it.channels }
    val unreadChannels = uiState.map { it.unreadChannels }
    val notification = uiState.map { it.notification }

    init {
        loadUser()
        loadWorkspace()
        loadChannels()
        loadNotifications()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val userId = userManager.getCurrentUserId()
            if (userId != null) {
                val response = userRepository.getUserByIdFromApi(userId)
                if (response.success && response.data != null) {
                    _uiState.update { it.copy(user = response.data) }
                }
            }
        }
    }

    private fun loadWorkspace() {
        viewModelScope.launch {
            val workspacesResponse = workspaceRepository.getAllWorkspacesFromApi()
            if (workspacesResponse.success && !workspacesResponse.data.isNullOrEmpty()) {
                _uiState.update { it.copy(workspace = workspacesResponse.data.first()) }
            }
        }
    }

    private fun loadChannels() {
        viewModelScope.launch {
            val workspaceId = _uiState.value.workspace._id
            if (workspaceId.isNotEmpty()) {
                val response = channelRepository.getChannelsByWorkspaceFromApi(workspaceId = workspaceId)
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            channels = response.data ?: emptyList(),
                            unreadChannels = (response.data ?: emptyList()).filter { channel -> /* logic xác định unread */ false }
                        )
                    }
                }
            }
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val response = notificationRepository.getUnreadNotificationsFromApi()
            if (response.success && response.data.isNotEmpty()) {
                _uiState.update { it.copy(notification = response.data.first().content) }
            } else {
                _uiState.update { it.copy(notification = "") }
            }
        }
    }
}