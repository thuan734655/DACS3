package com.example.dacs3.ui.auth.twofactor

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

const val TWO_FACTOR_AUTH_ROUTE = "2fa_verification"
const val EMAIL_ARG = "email"
const val PASSWORD_ARG = "password"

fun NavController.navigateToTwoFactorAuth(email: String, password: String? = null) {
    val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
    val route = if (password != null) {
        val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString())
        "$TWO_FACTOR_AUTH_ROUTE/$encodedEmail/$encodedPassword"
    } else {
        "$TWO_FACTOR_AUTH_ROUTE/$encodedEmail/null"
    }
    this.navigate(route)
}

fun NavGraphBuilder.twoFactorAuthScreen(
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable(
        route = "$TWO_FACTOR_AUTH_ROUTE/{$EMAIL_ARG}/{$PASSWORD_ARG}",
        arguments = listOf(
            navArgument(EMAIL_ARG) { type = NavType.StringType },
            navArgument(PASSWORD_ARG) { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val encodedEmail = backStackEntry.arguments?.getString(EMAIL_ARG) ?: ""
        val encodedPassword = backStackEntry.arguments?.getString(PASSWORD_ARG)

        val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
        val password = if (encodedPassword != "null") {
            URLDecoder.decode(encodedPassword, StandardCharsets.UTF_8.toString())
        } else null

        TwoFactorAuthScreen(
            email = email,
            password = password,
            onVerificationSuccess = onVerificationSuccess,
            onNavigateBack = onNavigateBack
        )
    }
}