package com.example.dacs3.ui.auth.otp

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.ui.auth.twofactor.TWO_FACTOR_AUTH_ROUTE

const val OTP_VERIFICATION_ROUTE = "otp_verification"
const val EMAIL_ARG = "email"
const val ACTION_ARG = "action"

fun NavController.navigateToOtpVerification(email: String, action: String? = null) {
    val route = if (action != null) {
        "$OTP_VERIFICATION_ROUTE/$email?$ACTION_ARG=$action"
    } else {
        "$OTP_VERIFICATION_ROUTE/$email"
    }
    this.navigate(route)
}

fun NavGraphBuilder.otpVerificationScreen(
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onTwoFactorRequired: (String) -> Unit = { email ->
        // Default implementation that can be overridden
    }
) {
    composable(
        route = "$OTP_VERIFICATION_ROUTE/{$EMAIL_ARG}?$ACTION_ARG={$ACTION_ARG}",
        arguments = listOf(
            navArgument(EMAIL_ARG) { type = NavType.StringType },
            navArgument(ACTION_ARG) { type = NavType.StringType; nullable = true; defaultValue = null }
        )
    ) { backStackEntry ->
        val email = backStackEntry.arguments?.getString(EMAIL_ARG) ?: ""
        val action = backStackEntry.arguments?.getString(ACTION_ARG)
        
        // Log the action for debugging
        Log.d("OtpNavigation", "Creating OtpScreen with email: $email, action: $action")
        
        // Create OtpScreen with email and action
        OtpScreen(
            email = email,
            action = action,
            onVerificationSuccess = onVerificationSuccess,
            onNavigateBack = onNavigateBack,
            onTwoFactorAuthRequired = { emailForVerification ->
                // Navigate to 2FA screen using parent navigation callback
                onTwoFactorRequired(emailForVerification)
            }
        )
    }
} 