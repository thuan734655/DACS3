package com.example.dacs3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onMessageClick: () -> Unit,
    onDashboardClick: () -> Unit = {},
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = currentRoute == "home",
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
            selected = currentRoute == "messages",
            onClick = onMessageClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Dashboard, contentDescription = "Dashboard") },
            selected = currentRoute == "dashboard",
            onClick = onDashboardClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = currentRoute == "profile",
            onClick = onProfileClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            selected = currentRoute == "settings",
            onClick = onSettingsClick
        )
    }
}