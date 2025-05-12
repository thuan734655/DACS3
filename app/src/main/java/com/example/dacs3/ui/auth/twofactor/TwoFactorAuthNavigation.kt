package com.example.dacs3.ui.auth.twofactor

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val TWO_FACTOR_AUTH_ROUTE = "2fa_verification"
const val EMAIL_ARG = "email"

fun NavController.navigateToTwoFactorAuth(email: String) {
    this.navigate("$TWO_FACTOR_AUTH_ROUTE/$email")
}

fun NavGraphBuilder.twoFactorAuthScreen(
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable(
        route = "$TWO_FACTOR_AUTH_ROUTE/{$EMAIL_ARG}",
        arguments = listOf(
            navArgument(EMAIL_ARG) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val email = backStackEntry.arguments?.getString(EMAIL_ARG) ?: ""
        TwoFactorAuthScreen(
            email = email,
            onVerificationSuccess = onVerificationSuccess,
            onNavigateBack = onNavigateBack
        )
    }
} 