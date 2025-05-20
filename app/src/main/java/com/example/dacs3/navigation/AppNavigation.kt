package com.example.dacs3.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.data.session.SessionManagerViewModel
import com.example.dacs3.ui.auth.ForgotPasswordScreen
import com.example.dacs3.ui.auth.LoginScreen
import com.example.dacs3.ui.auth.RegisterScreen
import com.example.dacs3.ui.auth.otp.otpVerificationScreen
import com.example.dacs3.ui.auth.otp.resetPasswordScreen
import com.example.dacs3.ui.auth.twofactor.twoFactorAuthScreen
import com.example.dacs3.ui.channels.ChannelsScreen
import com.example.dacs3.ui.dashboard.DashboardScreen
import com.example.dacs3.ui.epic.EpicScreen
import com.example.dacs3.ui.home.HomeViewModel
import com.example.dacs3.ui.onboarding.OnboardingScreen
import com.example.dacs3.ui.profile.ProfileScreen
import com.example.dacs3.ui.report.DailyReportScreen
import com.example.dacs3.ui.sprint.CreateSprintScreen
import com.example.dacs3.ui.sprint.EditSprintScreen
import com.example.dacs3.ui.sprint.SprintScreen
import com.example.dacs3.ui.task.CreateTaskScreen
import com.example.dacs3.ui.task.EditTaskScreen
import com.example.dacs3.ui.task.TaskDetailScreen
import com.example.dacs3.ui.task.TaskScreen
import com.example.dacs3.ui.welcome.WelcomeScreen
import com.example.dacs3.ui.workspace.WorkspaceScreen
import com.example.dacs3.ui.workspace.workspaceDetailScreen

// Navigation helper functions
private fun navigateToHome(navController: NavController) {
    navController.navigate(Screen.Home.route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}

private fun navigateToDashboard(navController: NavController) {
    navController.navigate(Screen.Dashboard.route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
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

    val uiState by viewModel.uiState.collectAsState()
    // Lấy workspace từ viewModel theo cách đúng
    val workspaceState by viewModel.workspace.collectAsState(initial = null)

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
        workspaceDetailScreen(navController)
        
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
        
//        // Add Epic detail screen route
//        composable(
//            route = Screen.EpicDetail.route,
//            arguments = listOf(navArgument("epicId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val epicId = backStackEntry.arguments?.getString("epicId") ?: ""
//
//            TaskScreen(
//                epicId = epicId,
//                onNavigateBack = { navController.popBackStack() },
//                onTaskSelected = { task ->
//                    navController.navigate(Screen.TaskDetail.createRoute(task._id))
//                }
//            )
//        }
//
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
                },
                onCreateSprint = { wsId ->
                    navController.navigate(Screen.CreateSprint.createRoute(wsId))
                }
            )
        }
        
        // Add Sprint detail screen route
        composable(
            route = Screen.SprintDetail.route,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            
            // Use the real SprintDetailScreen component
            com.example.dacs3.ui.sprint.SprintDetailScreen(
                sprintId = sprintId,
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        
        // Add EditSprint screen route
        composable(
            route = Screen.EditSprint.route,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            
            EditSprintScreen(
                sprintId = sprintId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Add CreateSprint screen route
        composable(
            route = Screen.CreateSprint.route,
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            
            CreateSprintScreen(
                workspaceId = workspaceId,
                onNavigateBack = { navController.popBackStack() },
                onSprintCreated = {
                    navController.popBackStack()
                }
            )
        }
        
        // Add EditSprint screen route
        composable(
            route = Screen.EditSprint.route,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            
            EditSprintScreen(
                sprintId = sprintId,
                onNavigateBack = { navController.popBackStack() }
            )
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
                // After successful OTP verification, navigate to home screen (for non-email verification)
                Log.d("AppNavigation", "OTP verification success (non-email), navigating to home screen")
                navController.navigate(Screen.Login.route) {
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
            },
            // Thêm callback để chuyển đến trang Login sau xác thực email
            onRedirectToLogin = {
                Log.d("AppNavigation", "Email verification success, redirecting to login screen")
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
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
                    // Navigate to board screen
                    if (workspaceState?._id.isNullOrEmpty()) {
                        Log.e("NavigationDebug", "Dashboard -> Board: workspaceId is null or empty")
                        // Show a toast notification to the user
                        android.widget.Toast.makeText(
                            navController.context,
                            "Please select a workspace first",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val workspaceId = workspaceState!!._id!!
                        try {
                            Log.d("NavigationDebug", "Dashboard -> Board: Navigating with workspaceId: $workspaceId")
                            navController.navigate("board/$workspaceId")
                        } catch (e: Exception) {
                            Log.e("NavigationDebug", "Dashboard -> Board: Navigation failed", e)
                            android.widget.Toast.makeText(
                                navController.context,
                                "Error navigating to board: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onSprintClick = {
                    // Navigate to sprint screen
                    if (workspaceState?._id.isNullOrEmpty()) {
                        Log.e("NavigationDebug", "Dashboard -> Sprint: workspaceId is null or empty")
                        // Show a toast notification to the user
                        android.widget.Toast.makeText(
                            navController.context,
                            "Please select a workspace first",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val workspaceId = workspaceState!!._id!!
                        try {
                            Log.d("NavigationDebug", "Dashboard -> Sprint: Navigating with workspaceId: $workspaceId")
                            navController.navigate(Screen.SprintList.createRoute(workspaceId))
                        } catch (e: Exception) {
                            Log.e("NavigationDebug", "Dashboard -> Sprint: Navigation failed", e)
                            android.widget.Toast.makeText(
                                navController.context,
                                "Error navigating to sprint: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onEpicClick = {
                    // Navigate to epic screen
                    if (workspaceState?._id.isNullOrEmpty()) {
                        Log.e("NavigationDebug", "Dashboard -> Epic: workspaceId is null or empty")
                        // Show a toast notification to the user
                        android.widget.Toast.makeText(
                            navController.context,
                            "Please select a workspace first",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val workspaceId = workspaceState!!._id!!
                        try {
                            Log.d("NavigationDebug", "Dashboard -> Epic: Navigating with workspaceId: $workspaceId")
                            navController.navigate(Screen.EpicList.createRoute(workspaceId))
                        } catch (e: Exception) {
                            Log.e("NavigationDebug", "Dashboard -> Epic: Navigation failed", e)
                            android.widget.Toast.makeText(
                                navController.context,
                                "Error navigating to epic: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onTaskClick = {
                    // Navigate to task screen
                    if (workspaceState?._id.isNullOrEmpty()) {
                        Log.e("NavigationDebug", "Dashboard -> Tasks: workspaceId is null or empty")
                        // Show a toast notification to the user
                        android.widget.Toast.makeText(
                            navController.context,
                            "Please select a workspace first",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val workspaceId = workspaceState!!._id!!
                        Log.d("NavigationDebug", "Dashboard -> Tasks: Attempting to navigate to tasks with workspaceId: $workspaceId")
                        try {
                            val route = Screen.TaskList.createRoute(workspaceId)
                            Log.d("NavigationDebug", "Dashboard -> Tasks: Generated route: $route")
                            navController.navigate(route)
                            Log.d("NavigationDebug", "Dashboard -> Tasks: Navigation completed successfully")
                        } catch (e: Exception) {
                            Log.e("NavigationDebug", "Dashboard -> Tasks: Navigation failed", e)
                            // Show error toast to user
                            android.widget.Toast.makeText(
                                navController.context,
                                "Error navigating to tasks: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onDashboardClick = {
                    // Already on dashboard
                },
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        // Add Board route
        composable(
            route = "board/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Board Screen for workspace: $workspaceId")
                // TODO: Implement proper Board screen
            }
        }
        
        // Add TaskList screen route
        composable(
            route = "tasks/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""

            TaskScreen(
                workspaceId = workspaceId,
                onNavigateBack = { navController.popBackStack() },
                onTaskSelected = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(Screen.CreateTask.createRoute(workspaceId, null))
                },
                onHomeClick = { navigateToHome(navController) },
                onDashboardClick = { navigateToDashboard(navController) },
                onProfileClick = { /* TODO */ }
            )
        }


        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onEditTask = { id ->
                    navController.navigate(Screen.EditTask.createRoute(id))
                }
            )
        }

        // CreateTask screen với xử lý lỗi đúng cách
        composable(
            route = Screen.CreateTask.route,
            arguments = listOf(
                navArgument("workspaceId") { type = NavType.StringType },
                navArgument("epicId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "null"
                }
            )
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            val epicId = backStackEntry.arguments?.getString("epicId")?.takeIf { it != "null" }
            
            Log.d("NavigationDebug", "CreateTask screen được khởi chạy với workspaceId: $workspaceId, epicId: $epicId")
            
            // Đảm bảo có workspaceId hợp lệ
            if (workspaceId.isEmpty()) {
                Log.e("NavigationDebug", "CreateTask screen nhận được workspaceId trống")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: Không có ID workspace được cung cấp")
                }
            } else {
                CreateTaskScreen(
                    workspaceId = workspaceId,
                    epicId = epicId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

            EditTaskScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
