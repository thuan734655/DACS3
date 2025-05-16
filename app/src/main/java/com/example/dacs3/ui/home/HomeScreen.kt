package com.example.dacs3.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dacs3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String,
    onNavigateToWorkspaces: (() -> Unit)? = null,
    onNavigateToEpics: (() -> Unit)? = null,
    onNavigateToSprints: (() -> Unit)? = null,
    onNavigateToMessages: (() -> Unit)? = null,
    onNavigateToNotifications: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null,
    onNavigateToMyTasks: (() -> Unit)? = null,
    onNavigateToDailyReport: (() -> Unit)? = null
) {
    var selectedItem by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        // If we're in workspace detail mode (onNavigateToEpics and onNavigateToSprints not null)
                        if (onNavigateToEpics != null && onNavigateToSprints != null) {
                            Text(
                                text = "Workspace",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = username,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(text = "Hello, $username")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    IconButton(onClick = { onNavigateToNotifications?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
                    label = { Text("Messages") },
                    selected = selectedItem == 1,
                    onClick = { 
                        selectedItem = 1
                        onNavigateToMessages?.invoke()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == 2,
                    onClick = { 
                        selectedItem = 2
                        onNavigateToProfile?.invoke()
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Feature grid
                if (onNavigateToEpics != null && onNavigateToSprints != null) {
                    // Workspace Detail Mode
                    Text(
                        text = "Manage Workspace",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FeatureCard(
                                icon = Icons.Default.FormatListBulleted,
                                title = "Epics",
                                onClick = onNavigateToEpics
                            )
                        }
                        item {
                            FeatureCard(
                                icon = Icons.Default.Timer,
                                title = "Sprints",
                                onClick = onNavigateToSprints
                            )
                        }
                        item {
                            FeatureCard(
                                icon = Icons.Default.Group,
                                title = "Members",
                                onClick = { /* TODO: Implement */ }
                            )
                        }
                        item {
                            FeatureCard(
                                icon = Icons.Default.BarChart,
                                title = "Reports",
                                onClick = { /* TODO: Implement */ }
                            )
                        }
                    }
                } else {
                    // Main Home Mode
                    Text(
                        text = "Quick Access",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FeatureCard(
                                icon = Icons.Default.Dashboard,
                                title = "Workspaces",
                                onClick = onNavigateToWorkspaces
                            )
                        }
                        item {
                            FeatureCard(
                                icon = Icons.Default.Chat,
                                title = "Messages",
                                onClick = onNavigateToMessages
                            )
                        }
                        item {
                            FeatureCard(
                                icon = Icons.Default.Assignment,
                                title = "My Tasks",
                                onClick = onNavigateToMyTasks
                            )
                        }
                        item {
                            FeatureCard(
                                icon = Icons.Default.BarChart,
                                title = "Báo cáo",
                                onClick = onNavigateToDailyReport
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recent tasks or activities
                Text(
                    text = "Recent Activities",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Placeholder for recent activities
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No recent activities",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
} 