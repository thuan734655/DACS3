package com.example.dacs3.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.ui.auth.ForgotPasswordScreen
import com.example.dacs3.ui.auth.LoginScreen
import com.example.dacs3.ui.auth.RegisterScreen
import com.example.dacs3.ui.auth.otp.otpVerificationScreen
import com.example.dacs3.ui.auth.otp.resetPasswordScreen
import com.example.dacs3.ui.auth.twofactor.twoFactorAuthScreen
import com.example.dacs3.ui.home.HomeScreen
import com.example.dacs3.ui.onboarding.OnboardingScreen
import com.example.dacs3.ui.profile.ProfileScreen
import com.example.dacs3.ui.welcome.WelcomeScreen

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
        
//        // Add Channels screen route
//        composable("channels") {
//            ChannelsScreen()
//        }
//
//        // Add Workspaces screen route
//        composable("workspaces") {
//            WorkspacesScreen()
//        }
        
        // Add Profile screen route
        composable("profile") {
            ProfileScreen()
        }
        
        // Add OTP verification screen route
        otpVerificationScreen(
            navController = navController,
            onVerificationSuccess = { source ->
                // After successful OTP verification, navigate based on source
                when (source) {
                    "register" -> {
                        // If OTP was called from register, navigate to login
                        Log.d("AppNavigation", "Email verification from register, navigating to login screen")
                        navController.navigate("login") {
                            popUpTo("otp_verification/{email}") { inclusive = true }
                        }
                    }
                    "login" -> {
                        // If OTP was called from login, navigate to home
                        Log.d("AppNavigation", "Email verification from login, navigating to home screen")
                        navController.navigate("home") {
                            popUpTo("otp_verification/{email}") { inclusive = true }
                        }
                    }
                    else -> {
                        // Default behavior (same as before)
                        Log.d("AppNavigation", "Email verification success, navigating to home screen")
                        navController.navigate("home") {
                            popUpTo("otp_verification/{email}") { inclusive = true }
                        }
                    }
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