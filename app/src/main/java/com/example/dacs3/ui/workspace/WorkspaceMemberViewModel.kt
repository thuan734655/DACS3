package com.example.dacs3.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.WorkspaceMember
import com.example.dacs3.data.repository.NotificationRepository
import com.example.dacs3.data.repository.UserRepository
import com.example.dacs3.data.repository.WorkspaceRepository
import com.example.dacs3.data.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkspaceMemberUiState(
    val isLoading: Boolean = false,
    val members: List<WorkspaceMember> = emptyList(),
    val availableUsers: List<User> = emptyList(),
    val error: String? = null,
    val isAddMemberSuccessful: Boolean = false,
    val isRemoveMemberSuccessful: Boolean = false,
    val isUpdateRoleSuccessful: Boolean = false,
    val selectedMember: WorkspaceMember? = null,
    val showRemoveMemberDialog: Boolean = false,
    val currentWorkspaceId: String? = null
)

@HiltViewModel
class WorkspaceMemberViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkspaceMemberUiState())
    val uiState: StateFlow<WorkspaceMemberUiState> = _uiState.asStateFlow()

    fun loadWorkspaceMembers(workspaceId: String) {
        _uiState.update { it.copy(isLoading = true, currentWorkspaceId = workspaceId) }
        viewModelScope.launch {
            try {
                val response = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
                if (response.success && response.data != null) {
                    val members = response.data.members ?: emptyList()
                    _uiState.update { it.copy(members = members, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Failed to load workspace members", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred", isLoading = false) }
            }
        }
    }

    fun loadAvailableUsers() {
        viewModelScope.launch {
            try {
                val response = userRepository.getAllUsersFromApi()
                if (response.success) {
                    // Filter out users who are already members of the workspace
                    val memberUserIds = _uiState.value.members.map { it.user_id._id }
                    val availableUsers = response.data.filter { user -> user._id !in memberUserIds }
                    _uiState.update { it.copy(availableUsers = availableUsers) }
                } else {
                    _uiState.update { it.copy(error = "Failed to load users") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred") }
            }
        }
    }

    fun addMember(userId: String, role: String) {
        val workspaceId = _uiState.value.currentWorkspaceId ?: return
        
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = workspaceRepository.addMember(workspaceId, userId, role)
                if (response.success && response.data != null) {
                    // Create notification for the added user
                    createAddMemberNotification(workspaceId, userId)
                    
                    // Refresh the members list
                    loadWorkspaceMembers(workspaceId)
                    _uiState.update { it.copy(isAddMemberSuccessful = true, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Failed to add member", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred", isLoading = false) }
            }
        }
    }

    fun removeMember(userId: String) {
        val workspaceId = _uiState.value.currentWorkspaceId ?: return
        
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = workspaceRepository.removeMember(workspaceId, userId)
                if (response.success && response.data != null) {
                    // Create notification for the removed user
                    createRemoveMemberNotification(workspaceId, userId)
                    
                    // Refresh the members list
                    loadWorkspaceMembers(workspaceId)
                    _uiState.update { it.copy(isRemoveMemberSuccessful = true, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Failed to remove member", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred", isLoading = false) }
            }
        }
    }

    fun updateMemberRole(userId: String, role: String) {
        val workspaceId = _uiState.value.currentWorkspaceId ?: return
        
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // First remove the member, then add them back with the new role
                val removeResponse = workspaceRepository.removeMember(workspaceId, userId)
                if (removeResponse.success) {
                    val addResponse = workspaceRepository.addMember(workspaceId, userId, role)
                    if (addResponse.success && addResponse.data != null) {
                        // Create notification for the role update
                        createRoleUpdateNotification(workspaceId, userId, role)
                        
                        // Refresh the members list
                        loadWorkspaceMembers(workspaceId)
                        _uiState.update { it.copy(isUpdateRoleSuccessful = true, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(error = "Failed to update member role", isLoading = false) }
                    }
                } else {
                    _uiState.update { it.copy(error = "Failed to update member role", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred", isLoading = false) }
            }
        }
    }

    // Notification functions
    private suspend fun createAddMemberNotification(workspaceId: String, userId: String) {
        try {
            val currentUserId = userManager.getCurrentUserId()
            val workspaceResponse = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
            
            if (currentUserId != null && workspaceResponse.success && workspaceResponse.data != null) {
                val workspace = workspaceResponse.data
                val userResponse = userRepository.getUserByIdFromApi(currentUserId)
                if (userResponse.success && userResponse.data != null) {
                    val currentUser = userResponse.data
                    val notificationContent = "You have been added to workspace ${workspace.name} by ${currentUser.name}"
                    
                    notificationRepository.createNotification(
                        userId = userId,
                        type = "workspace_member_added",
                        typeId = null,
                        workspaceId = workspaceId,
                        content = notificationContent,
                        relatedId = currentUser._id
                    )
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the main operation
            println("Failed to create notification: ${e.message}")
        }
    }

    private suspend fun createRemoveMemberNotification(workspaceId: String, userId: String) {
        try {
            val currentUserId = userManager.getCurrentUserId()
            val workspaceResponse = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
            
            if (currentUserId != null && workspaceResponse.success && workspaceResponse.data != null) {
                val workspace = workspaceResponse.data
                val userResponse = userRepository.getUserByIdFromApi(currentUserId)
                if (userResponse.success && userResponse.data != null) {
                    val currentUser = userResponse.data
                    val notificationContent = "You have been removed from workspace ${workspace.name} by ${currentUser.name}"
                    
                    notificationRepository.createNotification(
                        userId = userId,
                        type = "workspace_member_removed",
                        typeId = null,
                        workspaceId = workspaceId,
                        content = notificationContent,
                        relatedId = currentUser._id
                    )
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the main operation
            println("Failed to create notification: ${e.message}")
        }
    }

    private suspend fun createRoleUpdateNotification(workspaceId: String, userId: String, newRole: String) {
        try {
            val currentUserId = userManager.getCurrentUserId()
            val workspaceResponse = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
            
            if (currentUserId != null && workspaceResponse.success && workspaceResponse.data != null) {
                val workspace = workspaceResponse.data
                val userResponse = userRepository.getUserByIdFromApi(currentUserId)
                if (userResponse.success && userResponse.data != null) {
                    val currentUser = userResponse.data
                    val notificationContent = "Your role in workspace ${workspace.name} has been updated to ${newRole} by ${currentUser.name}"
                    
                    notificationRepository.createNotification(
                        userId = userId,
                        type = "workspace_role_updated",
                        typeId = null,
                        workspaceId = workspaceId,
                        content = notificationContent,
                        relatedId = currentUser._id
                    )
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the main operation
            println("Failed to create notification: ${e.message}")
        }
    }

    // UI state management functions
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(
            isAddMemberSuccessful = false,
            isRemoveMemberSuccessful = false,
            isUpdateRoleSuccessful = false
        ) }
    }

    fun setSelectedMember(member: WorkspaceMember) {
        _uiState.update { it.copy(selectedMember = member) }
    }

    fun showRemoveMemberDialog() {
        _uiState.update { it.copy(showRemoveMemberDialog = true) }
    }

    fun hideRemoveMemberDialog() {
        _uiState.update { it.copy(showRemoveMemberDialog = false) }
    }
}
