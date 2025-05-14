package com.example.dacs3.ui.auth.otp

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.ui.auth.ResetPasswordScreen
import com.example.dacs3.ui.auth.twofactor.TWO_FACTOR_AUTH_ROUTE

const val OTP_VERIFICATION_ROUTE = "otp_verification"
const val EMAIL_ARG = "email"
const val ACTION_ARG = "action"
const val SOURCE_ARG = "source"
const val RESET_PASSWORD_ROUTE = "reset_password"

fun NavController.navigateToOtpVerification(email: String, action: String? = null, source: String? = null) {
    val route = buildString {
        append("$OTP_VERIFICATION_ROUTE/$email")
        if (action != null || source != null) {
            append("?")
            if (action != null) {
                append("$ACTION_ARG=$action")
            }
            if (source != null) {
                if (action != null) append("&")
                append("$SOURCE_ARG=$source")
            }
        }
    }
    this.navigate(route)
}

fun NavController.navigateToResetPassword(email: String, otp: String) {
    this.navigate("$RESET_PASSWORD_ROUTE/$email/$otp")
}

fun NavGraphBuilder.otpVerificationScreen(
    navController: NavController,
    onVerificationSuccess: (String?) -> Unit,
    onNavigateBack: () -> Unit,
    onTwoFactorRequired: (String) -> Unit = { email ->
        // Default implementation that can be overridden
    }
) {
    composable(
        route = "$OTP_VERIFICATION_ROUTE/{$EMAIL_ARG}?$ACTION_ARG={$ACTION_ARG}&$SOURCE_ARG={$SOURCE_ARG}",
        arguments = listOf(
            navArgument(EMAIL_ARG) { type = NavType.StringType },
            navArgument(ACTION_ARG) { 
                type = NavType.StringType 
                nullable = true
                defaultValue = null
            },
            navArgument(SOURCE_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { entry ->
        val email = entry.arguments?.getString(EMAIL_ARG) ?: ""
        val action = entry.arguments?.getString(ACTION_ARG)
        val source = entry.arguments?.getString(SOURCE_ARG)
        
        Log.d("OtpNavigation", "Loading OTP screen with email: $email, action: $action, source: $source")
        
        OtpScreen(
            email = email,
            action = action,
            source = source,
            onVerificationSuccess = { 
                onVerificationSuccess(source)
            },
            onNavigateBack = onNavigateBack,
            onTwoFactorAuthRequired = { emailArg ->
                onTwoFactorRequired(emailArg)
            },
            onResetPassword = { emailArg, otpCode ->
                Log.d("OtpNavigation", "Navigating to reset password with email: $emailArg")
                navController.navigateToResetPassword(emailArg, otpCode)
            }
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