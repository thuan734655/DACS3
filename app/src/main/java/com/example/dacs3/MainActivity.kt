package com.example.dacs3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.dacs3.ui.nav.AuthNavGraph
import com.example.dacs3.ui.screens.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var isLoggedIn by remember { mutableStateOf(false) }
            var token by remember { mutableStateOf<String?>(null) }

            if (!isLoggedIn) {
                AuthNavGraph(navController) { receivedToken ->
                    token = receivedToken
                    // TODO: Lưu vào EncryptedSharedPreferences
                    isLoggedIn = true
                }
            } else {
                if (!isLoggedIn) {
                    AuthNavGraph(navController) { receivedToken ->
                        token = receivedToken
                        isLoggedIn = true
                    }
                } else {
                    HomeScreen()
                }
            }
        }
    }
}
