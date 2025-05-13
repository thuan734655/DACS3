package com.example.dacs3.data.model

import java.util.Date

// Home state model
data class HomeState(
    val currentWorkspace: Workspace? = null,
    val workspaces: List<Workspace> = emptyList(), // Thêm danh sách tất cả workspaces
    val channels: List<Channel> = emptyList(),
    val unreadChannels: List<Channel> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 