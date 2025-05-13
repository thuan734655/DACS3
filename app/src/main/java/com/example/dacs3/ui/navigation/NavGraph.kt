package com.example.dacs3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.ui.auth.LoginScreen
import com.example.dacs3.ui.auth.RegisterScreen
import com.example.dacs3.ui.auth.ForgotPasswordScreen
import com.example.dacs3.ui.auth.otp.OtpVerificationScreen
import com.example.dacs3.ui.home.HomeScreen
import com.example.dacs3.ui.onboarding.OnboardingScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }
        
        // Auth screens
        composable("login") {
            LoginScreen(navController = navController)
        }
        
        composable("register") {
            RegisterScreen(navController = navController)
        }
        
        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController)
        }
        
        // OTP verification with args
        composable(
            route = "otp_verification/{email}/{action}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("action") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val action = backStackEntry.arguments?.getString("action") ?: ""
            OtpVerificationScreen(
                navController = navController,
                email = email,
                action = action
            )
        }
        
        // Home screen
        composable("home") {
            HomeScreen(navController = navController)
        }
        
        // Create Workspace screen
        composable("create_workspace") {
            // TODO: Replace this with your actual CreateWorkspaceScreen when implemented
            androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }.value
            androidx.compose.material3.Text("Create Workspace Screen - To Be Implemented")
        }
    }
} 