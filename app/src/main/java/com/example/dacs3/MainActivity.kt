package com.example.dacs3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dacs3.navigation.AppNavigation
import com.example.dacs3.ui.auth.AuthViewModel
import com.example.dacs3.ui.theme.DACS3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DACS3Theme {
                MainAppScaffold()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Check if user is logged in to determine start destination
    val isLoggedIn = authViewModel.checkLoggedInStatus()
    val startDestination = if (isLoggedIn) "home" else "welcome"
    
    // Define screens that should show bottom navigation
    val showBottomBar by remember(currentDestination) {
        val hideBottomBarScreens = listOf(
            "login", "register", "welcome"
        )
        mutableStateOf(currentDestination?.route?.let { route ->
            !hideBottomBarScreens.any { screen -> route.startsWith(screen) }
        } ?: true)
    }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B4EFF)
                ) {
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                            }
                        },
                        label = { Text("Home") },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == "home" 
                        } == true,
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Chat"
                                )
                            }
                        },
                        label = { Text("Chat") },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == "channels" || 
                            it.route?.startsWith("direct_message") == true || 
                            it.route?.startsWith("channel") == true 
                        } == true,
                        onClick = {
                            navController.navigate("channels") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Workspaces"
                                )
                            }
                        },
                        label = { Text("Workspaces") },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == "workspaces" || 
                            it.route?.startsWith("workspace") == true || 
                            it.route?.startsWith("epic") == true || 
                            it.route?.startsWith("task") == true
                        } == true,
                        onClick = {
                            navController.navigate("workspaces") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile"
                                )
                            }
                        },
                        label = { Text("Profile") },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == "profile"
                        } == true,
                        onClick = {
                            navController.navigate("profile") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppNavigation(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}