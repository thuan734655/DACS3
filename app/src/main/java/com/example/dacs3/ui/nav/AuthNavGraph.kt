package com.example.dacs3.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dacs3.ui.screens.auth.LoginScreen
import com.example.dacs3.ui.screens.auth.OtpScreen
import com.example.dacs3.ui.screens.auth.RegisterScreen
import com.example.dacs3.viewmodel.AuthViewModel

@Composable
fun AuthNavGraph(
    navController: NavHostController = rememberNavController(),
    onLoginSuccess: (String) -> Unit
) {
    NavHost(navController, startDestination = "register") {
        composable("register") { backStackEntry ->
            val vm: AuthViewModel = hiltViewModel(backStackEntry)
            RegisterScreen(
                vm = vm,
                onNavigateOtp = { email ->
                    navController.navigate("otp/$email")
                },
                onNavigateLogin = {
                    navController.navigate("login")
                }
            )

        }
        composable(
            "otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = requireNotNull(backStackEntry.arguments?.getString("email"))
            val vm: AuthViewModel = hiltViewModel(backStackEntry)
            OtpScreen(
                email = email,
                vm = vm,
                onVerified = {
                    // Khi OTP đúng, chuyển tiếp sang login
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("login") { backStackEntry ->
            val vm: AuthViewModel = hiltViewModel(backStackEntry)
            LoginScreen(
                vm = vm,
                onLoginSuccess = onLoginSuccess,
                onNavigateRegister = {
                    navController.navigate("register")
                }
            )
        }

    }
}

