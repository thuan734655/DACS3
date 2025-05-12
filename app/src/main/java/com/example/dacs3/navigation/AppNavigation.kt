package com.example.dacs3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.example.dacs3.ui.profile.ProfileScreen
import com.example.dacs3.ui.welcome.WelcomeScreen
import com.example.dacs3.ui.workspaces.WorkspacesScreen

@Composable
fun AppNavigation(
    navController: NavHostController, 
    startDestination: String = "welcome",
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Always start with welcome or login, never directly to home
    // Remove automatic home navigation based on session
    
    // Get current user ID from AuthViewModel
    val currentUserId = authViewModel.currentUserId.collectAsState().value
    
    NavHost(navController = navController, startDestination = startDestination) {
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
            HomeScreen()
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
                // After successful OTP verification, navigate to login screen instead of home
                // This is appropriate for registration flow, as they'll need to login after verification
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
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
                // After successful 2FA verification, navigate to login screen
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
} 