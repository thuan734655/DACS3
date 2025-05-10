package com.example.dacs3.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.local.NotificationEntity
import com.example.dacs3.data.local.NotificationStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    userId: String? = null,
    onNavigateBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    var isMarkAllAsReadDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.loadNotifications(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isMarkAllAsReadDialogVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle, 
                            contentDescription = "Mark all as read"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (notifications.isEmpty()) {
                EmptyNotifications()
            } else {
                NotificationList(
                    notifications = notifications,
                    onMarkAsRead = { notification -> viewModel.markNotificationAsRead(notification.notificationId) },
                    onDelete = { notification -> viewModel.deleteNotification(notification) }
                )
            }
        }
    }

    if (isMarkAllAsReadDialogVisible) {
        AlertDialog(
            onDismissRequest = { isMarkAllAsReadDialogVisible = false },
            title = { Text("Mark all notifications as read?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        userId?.let { nonNullUserId ->
                            viewModel.markAllNotificationsAsRead(nonNullUserId)
                        }
                        isMarkAllAsReadDialogVisible = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isMarkAllAsReadDialogVisible = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyNotifications() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No notifications",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You're all caught up!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
fun NotificationList(
    notifications: List<NotificationEntity>,
    onMarkAsRead: (NotificationEntity) -> Unit,
    onDelete: (NotificationEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(notifications) { notification ->
            NotificationItem(
                notification = notification,
                onMarkAsRead = onMarkAsRead,
                onDelete = onDelete
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onMarkAsRead: (NotificationEntity) -> Unit,
    onDelete: (NotificationEntity) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(notification.createdAt))
    val backgroundColor = if (notification.status == NotificationStatus.UNREAD) {
        Color(0xFFEEF2FF)
    } else {
        Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = { if (notification.status == NotificationStatus.UNREAD) onMarkAsRead(notification) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.type.replaceFirstChar { 
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.status == NotificationStatus.UNREAD) 
                        FontWeight.Bold else FontWeight.Normal
                )
                
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = notification.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (notification.status == NotificationStatus.UNREAD) {
                    TextButton(onClick = { onMarkAsRead(notification) }) {
                        Text("Mark as read")
                    }
                }
                
                IconButton(onClick = { onDelete(notification) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete notification",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
} 