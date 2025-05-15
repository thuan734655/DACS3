package com.example.dacs3.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.dacs3.ui.home.components.ActivityStreamSection
import com.example.dacs3.ui.home.components.BottomNavigation
import com.example.dacs3.ui.home.components.ChannelsSection
import com.example.dacs3.ui.home.components.HeaderSection
import com.example.dacs3.ui.home.components.UnreadsSection
import com.example.dacs3.ui.theme.DACS3Theme

@Composable
fun HomeScreen(
    username: String = "User",
    modifier: Modifier = Modifier
) {
    var showNotifications by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = { BottomNavigation() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header at the top
            HeaderSection(
                username = username,
                onNotificationClick = { showNotifications = !showNotifications }
            )
            
            // Content in scrollable column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                ChannelsSection(
                    modifier = Modifier.padding(top = 8.dp)
                )
                UnreadsSection()
                ActivityStreamSection()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DACS3Theme {
        HomeScreen()
    }
}