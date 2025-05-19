package com.example.dacs3.ui.auth.otp

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.ui.auth.ResetPasswordScreen

const val OTP_VERIFICATION_ROUTE = "otp_verification"
const val EMAIL_ARG = "email"
const val ACTION_ARG = "action"
const val PASSWORD_ARG = "password"
const val RESET_PASSWORD_ROUTE = "reset_password"

fun NavController.navigateToOtpVerification(email: String, action: String? = null, password: String? = null) {
    var route = "$OTP_VERIFICATION_ROUTE/$email"
    
    // Build query parameters
    val queryParams = mutableListOf<String>()
    
    if (action != null) {
        queryParams.add("$ACTION_ARG=$action")
    }
    
    if (password != null) {
        queryParams.add("$PASSWORD_ARG=$password")
    }
    
    // Add query parameters to route if any exist
    if (queryParams.isNotEmpty()) {
        route += "?${queryParams.joinToString("&")}"
    }
    
    this.navigate(route)
}

fun NavController.navigateToResetPassword(email: String, otp: String) {
    this.navigate("$RESET_PASSWORD_ROUTE/$email/$otp")
}

fun NavGraphBuilder.otpVerificationScreen(
    navController: NavController,
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onTwoFactorRequired: (String) -> Unit = { email -> },
    onRedirectToLogin: () -> Unit = {
        // Default implementation navigates to login screen
        navController.navigate("login") {
            popUpTo("welcome") { inclusive = true }
        }
    }
) {
    composable(
        route = "$OTP_VERIFICATION_ROUTE/{$EMAIL_ARG}?$ACTION_ARG={$ACTION_ARG}&$PASSWORD_ARG={$PASSWORD_ARG}",
        arguments = listOf(
            navArgument(EMAIL_ARG) { type = NavType.StringType },
            navArgument(ACTION_ARG) { 
                type = NavType.StringType 
                nullable = true
                defaultValue = null
            },
            navArgument(PASSWORD_ARG) { 
                type = NavType.StringType 
                nullable = true
                defaultValue = null
            }
        )
    ) { entry ->
        val email = entry.arguments?.getString(EMAIL_ARG) ?: ""
        val action = entry.arguments?.getString(ACTION_ARG)
        val password = entry.arguments?.getString(PASSWORD_ARG)
        
        Log.d("OtpNavigation", "Loading OTP screen with email: $email, action: $action, password: ${if (password != null) "provided" else "not provided"}")
        
        OtpScreen(
            email = email,
            action = action,
            password = password,
            onVerificationSuccess = onVerificationSuccess,
            onNavigateBack = onNavigateBack,
            onTwoFactorAuthRequired = { emailArg ->
                onTwoFactorRequired(emailArg)
            },
            onResetPassword = { emailArg, otpCode ->
                // Handle navigation to reset password screen
                Log.d("OtpNavigation", "Navigating to reset password with email: $emailArg")
                navController.navigateToResetPassword(emailArg, otpCode)
            },
            onRedirectToLogin = {
                Log.d("OtpNavigation", "Redirecting to login screen after email verification")
                onRedirectToLogin()
            },
            navController = navController
        )
    }
}

fun NavGraphBuilder.resetPasswordScreen(
    navController: NavController,
    onResetSuccess: () -> Unit
) {
    composable(
        route = "$RESET_PASSWORD_ROUTE/{$EMAIL_ARG}/{otp}",
        arguments = listOf(
            navArgument(EMAIL_ARG) { type = NavType.StringType },
            navArgument("otp") { type = NavType.StringType }
        )
    ) { entry ->
        val email = entry.arguments?.getString(EMAIL_ARG) ?: ""
        val otp = entry.arguments?.getString("otp") ?: ""
        
        ResetPasswordScreen(
            email = email,
            otp = otp,
            navController = navController
        )
    }
} 