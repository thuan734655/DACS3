package com.example.dacs3.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.example.dacs3.ui.workspaces.WorkspacesScreen
import com.example.dacs3.ui.workspaces.create.CreateWorkspaceScreen
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
            HomeScreen(navController = navController)
        }
        
        // Add Channels screen route
        composable("channels") {
            ChannelsScreen()
        }
        
        // Add Workspaces screen route
        composable("workspaces") {
            WorkspacesScreen()
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
        
        // CreateWorkspace is now handled via dialog, no need for navigation route
    }
} 