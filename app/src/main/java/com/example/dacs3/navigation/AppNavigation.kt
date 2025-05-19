package com.example.dacs3.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.data.session.SessionManagerViewModel
import com.example.dacs3.ui.auth.AuthViewModel
import com.example.dacs3.ui.auth.ForgotPasswordScreen
import com.example.dacs3.ui.auth.LoginScreen
import com.example.dacs3.ui.auth.RegisterScreen
import com.example.dacs3.ui.auth.otp.OtpScreen
import com.example.dacs3.ui.auth.otp.otpVerificationScreen
import com.example.dacs3.ui.auth.otp.resetPasswordScreen
import com.example.dacs3.ui.auth.twofactor.twoFactorAuthScreen
import com.example.dacs3.ui.channels.ChannelsScreen
import com.example.dacs3.ui.dashboard.DashboardScreen
import com.example.dacs3.ui.epic.EpicScreen
import com.example.dacs3.ui.home.HomeScreen
import com.example.dacs3.ui.home.HomeViewModel
import com.example.dacs3.ui.onboarding.OnboardingScreen
import com.example.dacs3.ui.profile.ProfileScreen
import com.example.dacs3.ui.report.DailyReportScreen
import com.example.dacs3.ui.sprint.SprintScreen
import com.example.dacs3.ui.task.TaskScreen
import com.example.dacs3.ui.welcome.WelcomeScreen
import com.example.dacs3.ui.workspace.WorkspaceScreen
import com.example.dacs3.ui.workspace.WorkspaceViewModel
import com.example.dacs3.util.WorkspacePreferences
import javax.inject.Inject

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    sessionManager: SessionManagerViewModel
) {
    // Determine initial destination based on if it's first time using the app
    val initialDestination = if (sessionManager.isFirstTimeUser()) {
        Screen.Onboarding.route
    } else if (sessionManager.isLoggedIn()) {
        Screen.Home.route
    } else {
        Screen.Welcome.route
    }

    val homeViewModel: HomeViewModel = hiltViewModel()

    // Get current user ID from AuthViewModel
    val currentUserId = authViewModel.currentUserId.collectAsState().value
//    val currentUser = authViewModel.currentUser.collectAsState().value
    
    NavHost(navController = navController, startDestination = initialDestination) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        
        // Add ForgotPassword screen route
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }
        
        // Add Home screen route
        composable(Screen.Home.route) {
            HomeNavScreen(navController = navController)
        }
        
        // Add Channels screen route
        composable("channels") {
            ChannelsScreen()
        }
        
        // Add Workspaces screen route
        composable(Screen.WorkspaceList.route) {
            WorkspaceScreen(
                onNavigateBack = { navController.popBackStack() },
                onWorkspaceSelected = { workspace ->
                    // Navigate to workspace detail screen
                    navController.navigate(Screen.WorkspaceDetail.createRoute(workspace._id))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Clear back stack up to Home
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Add workspace detail screen route
        composable(
            route = Screen.WorkspaceDetail.route,
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            
            // Get workspace name
            val workspaceName = remember { mutableStateOf("Workspace") }
            val workspaceViewModel: WorkspaceViewModel = hiltViewModel()
            
            LaunchedEffect(workspaceId) {
                workspaceViewModel.getWorkspaceById(workspaceId)
            }
            
            val workspaceState by workspaceViewModel.uiState.collectAsState()
            
            // Update workspace name when loaded
            LaunchedEffect(workspaceState.selectedWorkspace) {
                workspaceState.selectedWorkspace?.let {
                    workspaceName.value = it.name
                }
            }
            
            // Workspace Detail Screen implementation

        }
        
        // Add Epic list screen route
        composable(
            route = Screen.EpicList.route,
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            
            EpicScreen(
                workspaceId = workspaceId,
                onNavigateBack = { navController.popBackStack() },
                onEpicSelected = { epic ->
                    navController.navigate(Screen.EpicDetail.createRoute(epic._id))
                }
            )
        }
        
        // Add Epic detail screen route
        composable(
            route = Screen.EpicDetail.route,
            arguments = listOf(navArgument("epicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val epicId = backStackEntry.arguments?.getString("epicId") ?: ""
            
            TaskScreen(
                epicId = epicId,
                onNavigateBack = { navController.popBackStack() },
                onTaskSelected = { task ->
                    navController.navigate(Screen.TaskDetail.createRoute(task._id))
                }
            )
        }
        
        // Add Sprint list screen route
        composable(
            route = Screen.SprintList.route,
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            
            SprintScreen(
                workspaceId = workspaceId,
                onNavigateBack = { navController.popBackStack() },
                onSprintSelected = { sprint ->
                    // Navigate to sprint detail
                    navController.navigate(Screen.SprintDetail.createRoute(sprint._id))
                }
            )
        }
        
        // Add Sprint detail screen route (placeholder for now)
        composable(
            route = Screen.SprintDetail.route,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            
            // For now, just show a placeholder screen
            // This is where you'd show tasks in the sprint
            Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Sprint Detail: $sprintId")
            }
        }
        
        // Add Task detail screen route (placeholder for now)
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            
            // For now, just show a placeholder screen
            Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Task Detail: $taskId")
            }
        }
        
        // Add Profile screen route
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMessages = {
                    navController.navigate(Screen.ConversationList.route)
                }
            )
        }
        
        // Add DailyReport screen route
        composable(Screen.DailyReport.route) {
            DailyReportScreen(
                onNavigateBack = { navController.popBackStack() },
                onTaskSelected = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMessages = {
                    navController.navigate(Screen.ConversationList.route)
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        // Setup OTP verification routes
        otpVerificationScreen(
            navController = navController,
            onVerificationSuccess = {
                // After successful OTP verification, navigate to home screen
                Log.d("AppNavigation", "Email verification success, navigating to home screen")
                navController.navigate(Screen.Home.route) {
                    // Don't clear too much back stack
                    popUpTo(Screen.OtpVerification.route) { inclusive = true }
                }
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onTwoFactorRequired = { email ->
                // Navigate to 2FA screen
                navController.navigate(Screen.TwoFactorAuth.createRoute(email)) {
                    // Clear back stack up to OTP screen to prevent returning to it
                    popUpTo(Screen.OtpVerification.createRoute(email)) { inclusive = true }
                }
            }
        )
        
        // Add Reset Password screen route
        resetPasswordScreen(
            navController = navController,
            onResetSuccess = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            }
        )
        
        // Add Two Factor Authentication screen route
        twoFactorAuthScreen(
            onVerificationSuccess = {
                // After successful 2FA verification, navigate to home screen
                Log.d("AppNavigation", "2FA success, navigating to home screen")
                navController.navigate(Screen.Home.route) {
                    // Don't clear too much back stack to preserve login flow
                    popUpTo(Screen.TwoFactorAuth.route) { inclusive = true }
                }
            },
            onNavigateBack = {
                navController.navigate("login")
            }
        )

        // Add Notifications screen route
        composable(Screen.Notifications.route) {
            com.example.dacs3.ui.notification.NotificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Add profile screen route
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        // Clear the back stack so user can't navigate back after logout
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Thêm màn hình chi tiết kênh
        composable(
                    route = "channel_detail/{channelId}",
                    arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
        
        // Placeholder cho màn hình chi tiết kênh
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
            Text("Channel Detail: $channelId")
            }
        }
        
        // Thêm màn hình tạo kênh mới
        composable(
                route = "create_channel/{workspaceId}",
                arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
        
        // Placeholder cho màn hình tạo kênh
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
        Text("Create Channel for Workspace: $workspaceId")
        }
        }
        
        // Thêm màn hình cài đặt
        composable("settings") {
        // Placeholder cho màn hình cài đặt
        Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
        ) {
        Text("Settings Screen")
        }
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onBoardClick = { 
                    // Điều hướng tới Board
                    navController.navigate(Screen.Board.route) 
                },
                onSprintClick = {
                    val workspaceId = homeViewModel.uiState.value.workspace._id
                    navController.navigate(Screen.SprintList.createRoute(workspaceId))
                },
                onEpicClick = { 
                    val workspaceId = homeViewModel.uiState.value.workspace._id
                    navController.navigate(Screen.EpicList.createRoute(workspaceId))
                },
                onTaskClick = { 
                    val workspaceId = homeViewModel.uiState.value.workspace._id
                    navController.navigate(Screen.WorkspaceTasks.createRoute(workspaceId))
                },
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onMessageClick = { navController.navigate(Screen.ConversationList.route) },
                onDashboardClick = { /* Đã ở Dashboard, không cần điều hướng */ },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
            )
        }
    }
}



