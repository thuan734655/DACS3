package com.example.dacs3.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dacs3.ui.screens.auth.LoginScreen
import com.example.dacs3.ui.screens.auth.OtpScreen
import com.example.dacs3.ui.screens.auth.RegisterScreen
import com.example.dacs3.ui.screens.home.HomeScreen
import com.example.dacs3.ui.screens.home.ChatScreen
import com.example.dacs3.ui.screens.home.TaskDetailScreen
import com.example.dacs3.ui.screens.home.WorkspaceDetailScreen
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.viewmodel.HomeViewModel
import com.example.dacs3.viewmodel.OtpViewModel

@Composable
fun AuthNavGraph(
    navController: NavHostController = rememberNavController(),
    onLoginSuccess: (String) -> Unit
) {
    NavHost(navController, startDestination = "login") {
        composable("register") { backStackEntry ->
            val vm: AuthViewModel = hiltViewModel(backStackEntry)
            RegisterScreen(
                vm = vm,
                onNavigateOtp = { email ->
                    navController.navigate("otp/$email")
                },
                onNavigateLogin = {
                    navController.navigate("login")
                }
            )
        }

        composable(
            "otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = requireNotNull(backStackEntry.arguments?.getString("email"))
            val vm: OtpViewModel = hiltViewModel(backStackEntry)
            OtpScreen(
                email = email,
                vm = vm,
                onVerified = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("login") { backStackEntry ->
            val vm: AuthViewModel = hiltViewModel(backStackEntry)
            LoginScreen(
                vm = vm,
                nav = navController,
                onLoginSuccess = { token ->
                    onLoginSuccess(token)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Home and related screens
        composable("home") { backStackEntry ->
            val vm: HomeViewModel = hiltViewModel(backStackEntry)
            HomeScreen(
                onNavigateToChat = { chatId ->
                    navController.navigate("chat/$chatId")
                },
                onNavigateToTask = { taskId ->
                    navController.navigate("task/$taskId")
                },
                onNavigateToWorkspace = { workspaceId ->
                    navController.navigate("workspace/$workspaceId")
                },
                viewModel = vm
            )
        }

        composable(
            "chat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = requireNotNull(backStackEntry.arguments?.getString("chatId"))
            ChatScreen(
                chatId = chatId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "task/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = requireNotNull(backStackEntry.arguments?.getString("taskId"))
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "workspace/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = requireNotNull(backStackEntry.arguments?.getString("workspaceId"))
            WorkspaceDetailScreen(
                workspaceId = workspaceId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

