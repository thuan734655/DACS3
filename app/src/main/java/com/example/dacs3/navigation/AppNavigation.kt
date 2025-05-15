package com.example.dacs3.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.ui.auth.AuthViewModel
import com.example.dacs3.ui.auth.ForgotPasswordScreen
import com.example.dacs3.ui.auth.LoginScreen
import com.example.dacs3.ui.auth.RegisterScreen
import com.example.dacs3.ui.auth.otp.OtpScreen
import com.example.dacs3.ui.auth.otp.otpVerificationScreen
import com.example.dacs3.ui.auth.otp.resetPasswordScreen
import com.example.dacs3.ui.auth.twofactor.twoFactorAuthScreen
import com.example.dacs3.ui.channels.ChannelsScreen
import com.example.dacs3.ui.home.HomeScreen
import com.example.dacs3.ui.onboarding.OnboardingScreen
import com.example.dacs3.ui.profile.ProfileScreen
import com.example.dacs3.ui.welcome.WelcomeScreen
import com.example.dacs3.ui.workspace.WorkspaceScreen
import javax.inject.Inject

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    sessionManager: SessionManager
) {
    // Determine initial destination based on if it's first time using the app
    val initialDestination = if (sessionManager.isFirstTimeUser()) {
        "onboarding"
    } else if (sessionManager.isLoggedIn()) {
        "home"
    } else {
        "welcome"
    }
    
    // Get current user ID from AuthViewModel
    val currentUserId = authViewModel.currentUserId.collectAsState().value
//    val currentUser = authViewModel.currentUser.collectAsState().value
    
    NavHost(navController = navController, startDestination = initialDestination) {
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }
        
        composable("welcome") {
            WelcomeScreen(navController)
        }
        
        composable("login") {
            LoginScreen(navController)
        }
        
        composable("register") {
            RegisterScreen(navController)
        }
        
        // Add ForgotPassword screen route
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }
        
        // Add Home screen route
        composable("home") {
            HomeScreen(
                username =  "User"
            )
        }
        
        // Add Channels screen route
        composable("channels") {
            ChannelsScreen()
        }
        
        // Add Workspaces screen route
        composable("workspaces") {
            WorkspaceScreen(
                onNavigateBack = { navController.popBackStack() },
                onWorkspaceSelected = { workspace ->
                    // Select workspace and navigate back to home
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_workspace", workspace)
                    navController.popBackStack()
                }
            )
        }
        
        // Add Profile screen route
        composable("profile") {
            ProfileScreen()
        }
        
        // Add OTP verification screen route
        otpVerificationScreen(
            navController = navController,
            onVerificationSuccess = {
                // After successful OTP verification, navigate to home screen
                Log.d("AppNavigation", "Email verification success, navigating to home screen")
                navController.navigate("home") {
                    // Don't clear too much back stack
                    popUpTo("otp_verification/{email}") { inclusive = true }
                }
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onTwoFactorRequired = { email ->
                // Navigate to 2FA screen
                navController.navigate("2fa_verification/$email") {
                    // Clear back stack up to OTP screen to prevent returning to it
                    popUpTo("otp_verification/${email}") { inclusive = true }
                }
            }
        )
        
        // Add Reset Password screen route
        resetPasswordScreen(
            navController = navController,
            onResetSuccess = {
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        )
        
        // Add Two Factor Authentication screen route
        twoFactorAuthScreen(
            onVerificationSuccess = {
                // After successful 2FA verification, navigate to home screen
                Log.d("AppNavigation", "2FA success, navigating to home screen")
                navController.navigate("home") {
                    // Don't clear too much back stack to preserve login flow
                    popUpTo("2fa_verification/{email}") { inclusive = true }
                }
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
} 