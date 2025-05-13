package com.example.dacs3.ui.auth.otp

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun OtpVerificationScreen(
    navController: NavController,
    email: String,
    action: String
) {
    OtpScreen(
        email = email,
        action = action,
        onVerificationSuccess = {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        },
        onNavigateBack = {
            navController.popBackStack()
        },
        onTwoFactorAuthRequired = { emailArg ->
            navController.navigate("2fa_verification/$emailArg")
        },
        onResetPassword = { emailArg, otpCode ->
            navController.navigate("reset_password/$emailArg/$otpCode")
        }
    )
} 