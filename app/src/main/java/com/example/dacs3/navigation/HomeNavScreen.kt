package com.example.dacs3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.dacs3.ui.home.HomeScreen
import com.example.dacs3.ui.home.HomeViewModel
import com.example.dacs3.ui.workspace.navigateToWorkspaceDetail

@Composable
fun HomeNavScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Lắng nghe uiState
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        user = uiState.user,
        workspace = uiState.workspace,
        allWorkspaces = uiState.allWorkspaces,
        channels = uiState.channels,
        unreadChannels = uiState.unreadChannels,
        notification = uiState.notification,
        onChannelClick = { channelId ->
            // Điều hướng đến màn hình chi tiết kênh
            navController.navigate("channel_detail/$channelId")
        },
        onAddChannel = {
            navController.navigate("create_channel/${uiState.workspace._id}")
        },
        onWorkspaceSelected = { workspaceId ->
            viewModel.selectWorkspace(workspaceId)
        },
        onWorkspaceDetailClick = { workspaceId ->
            navController.navigateToWorkspaceDetail(workspaceId)
        },
        onNotificationClick = {
            // Điều hướng đến màn hình thông báo
            navController.navigate(Screen.Notifications.route)
        },
        onHomeClick = {
            // Đã ở màn hình Home nên không cần điều hướng
        },
        onMessageClick = {
            // Điều hướng đến màn hình tin nhắn
            navController.navigate(Screen.ConversationList.route)
        },
        onDashboardClick = {
            // Điều hướng đến màn hình dashboard
            navController.navigate(Screen.Dashboard.route)
        },
        onProfileClick = {
            // Điều hướng đến màn hình hồ sơ
            navController.navigate(Screen.Profile.route)
        },
        oncreateWorkspaceClick = { name, description ->
            viewModel.createWorkspace(name, description)
        },
        onclickCreateChannel = { name, description, isPrivate ->
            viewModel.createChannel(name, description, isPrivate)
        }
    )
}
