package com.example.dacs3.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.model.User
import com.example.dacs3.ui.components.BottomNavigationBar

@Composable
fun HomeScreen(
    user: User?,
    workspace: Workspace?,
    channels: List<Channel>,
    unreadChannels: List<Channel>,
    notification: String,
    onChannelClick: (String) -> Unit,
    onAddChannel: () -> Unit,
    onNavigateToWorkspaces: () -> Unit,
    onNotificationClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMessageClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (header, channelSection, unreadSection, divider1, divider2, activitySection, bottomNav) = createRefs()

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .constrainAs(header) {
                    top.linkTo(parent.top, margin = 30.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(user?.avatar),
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Hello ${user?.name ?: ""}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Workspace name clickable
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            workspace?.name ?: "Your Workspace",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onNavigateToWorkspaces() }
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { onNotificationClick() }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF673AB7)
                        )
                    }
                }
            }
        }

        // Channels section
        Column(
            modifier = Modifier
                .constrainAs(channelSection) {
                    top.linkTo(header.bottom, margin = 24.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Channels",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Expand channels",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            if (channels.isEmpty()) {
                Text(
                    "No channels",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                channels.forEach { ch ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChannelClick(ch._id) }
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "# ${ch.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Divider 1
        Divider(
            modifier = Modifier
                .constrainAs(divider1) {
                    top.linkTo(channelSection.bottom, margin = 12.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                },
            color = Color.LightGray,
            thickness = 1.dp
        )

        // Unread section
        Column(
            modifier = Modifier
                .constrainAs(unreadSection) {
                    top.linkTo(divider1.bottom, margin = 12.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        ) {
            Text(
                "Unreads",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            if (unreadChannels.isEmpty()) {
                Text(
                    "No unread",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                unreadChannels.forEach { ch ->
                    Text(
                        "# ${ch.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }

            // Add channel button
            Text(
                "+ Add channel",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier
                    .clickable { onAddChannel() }
                    .padding(vertical = 6.dp)
            )
        }

        // Divider 2
        Divider(
            modifier = Modifier
                .constrainAs(divider2) {
                    top.linkTo(unreadSection.bottom, margin = 12.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                },
            color = Color.LightGray,
            thickness = 1.dp
        )

        // Activity stream
        Column(
            modifier = Modifier
                .constrainAs(activitySection) {
                    top.linkTo(divider2.bottom, margin = 12.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Activity stream",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Expand activity",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (notification.isEmpty()) "No notification!" else notification,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = "home",
            onHomeClick = onHomeClick,
            onMessageClick = onMessageClick,
            onDashboardClick = onDashboardClick,
            onProfileClick = onProfileClick,
            onSettingsClick = onSettingsClick,
            modifier = Modifier
                .constrainAs(bottomNav) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        )
    }
}
