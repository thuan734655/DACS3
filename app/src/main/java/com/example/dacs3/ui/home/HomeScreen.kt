package com.example.dacs3.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.R
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.ui.theme.MediumGrey
import com.example.dacs3.ui.theme.TeamNexusPurple
import com.example.dacs3.ui.theme.TextDark
import com.example.dacs3.viewmodel.ChannelViewModel
import com.example.dacs3.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    userName: String,
    workspaceName: String,
    onWorkspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar - now on the left
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TeamNexusPurple)
                    .clickable { /* User profile action */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.first().toString().uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp // Tăng font size
                )
            }

            // Workspace info - clickable to show workspace sidebar
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clickable { onWorkspaceClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = workspaceName,
                        fontSize = 18.sp, // Tăng font size
                        fontWeight = FontWeight.Bold, // Đổi thành Bold
                        color = TeamNexusPurple // Sử dụng màu chính
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show workspaces",
                        tint = TeamNexusPurple, // Đổi màu sang màu chính
                        modifier = Modifier.size(20.dp) // Tăng kích thước
                    )
                }

                Text(
                    text = "Hello $userName",
                    fontSize = 15.sp, // Tăng font size nhẹ
                    fontWeight = FontWeight.SemiBold, // Đổi thành SemiBold
                    color = TextDark // Sử dụng màu đậm hơn
                )
            }

            // Notification icon on the right
            IconButton(onClick = { /* Notification action */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = TeamNexusPurple, // Đổi màu sang màu chính
                    modifier = Modifier.size(28.dp) // Tăng kích thước
                )
            }
        }
    }
}

// Chỉnh sửa text trong HomeContent để nổi bật hơn
@Composable
fun HomeContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Channels section
        ExpandableSection(
            title = "Channels",
            initialExpanded = true,
            content = {
                ChannelsList(
                    channels = listOf("abc-xyz", "abc-xyz", "abc-xyz")
                )
            }
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        // Unreads section
        Text(
            text = "Unreads",
            fontSize = 18.sp, // Tăng font size
            fontWeight = FontWeight.Bold, // Đổi thành Bold
            color = TeamNexusPurple, // Sử dụng màu chính
            modifier = Modifier.padding(vertical = 12.dp)
        )

        ChannelItem(channelName = "abc-xyz")

        // Add channel button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { /* Add channel action */ }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add channel",
                tint = TeamNexusPurple,
                modifier = Modifier.size(20.dp) // Tăng kích thước
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Add channel",
                fontSize = 15.sp, // Tăng font size
                fontWeight = FontWeight.SemiBold, // Đổi thành SemiBold
                color = TeamNexusPurple
            )
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        // Activity stream section
        ExpandableSection(
            title = "Activity stream",
            initialExpanded = true,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notification!",
                        color = TextDark, // Đổi sang màu đậm hơn
                        fontSize = 16.sp, // Tăng font size
                        fontWeight = FontWeight.Medium // Đổi font weight
                    )
                }
            }
        )
    }
}

// Chỉnh sửa ExpandableSection để nổi bật hơn
@Composable
fun ExpandableSection(
    title: String,
    initialExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initialExpanded) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp, // Tăng font size
                fontWeight = FontWeight.Bold, // Đổi thành Bold
                color = TeamNexusPurple, // Sử dụng màu chính
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = TeamNexusPurple, // Đổi sang màu chính
                modifier = Modifier.size(24.dp) // Tăng kích thước
            )
        }

        AnimatedVisibility(visible = expanded) {
            content()
        }
    }
}

// Chỉnh sửa ChannelItem để nổi bật hơn
@Composable
fun ChannelItem(
    channelName: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Channel click action */ }
    ) {
        Text(
            text = "# $channelName",
            fontSize = 16.sp, // Tăng font size
            fontWeight = FontWeight.Medium, // Đổi thành Medium
            color = TextDark // Sử dụng màu đậm hơn
        )
    }
}

// Chỉnh sửa WorkspaceSidebar để nổi bật text hơn
@Composable
fun WorkspaceSidebar(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }
    ) {
        // Sidebar container - đặt ở dưới cùng
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFFF2EFFA))
                .clickable(onClick = {}) // Intercept clicks
        ) {
            // Header with handle for dragging
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
            }

            // Sidebar header
            Text(
                text = "Workspace",
                fontSize = 22.sp, // Tăng font size
                fontWeight = FontWeight.Bold,
                color = TeamNexusPurple, // Sử dụng màu chính
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
            )

            // Add workspace button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { /* Add workspace action */ }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add workspace",
                        tint = TeamNexusPurple, // Đổi sang màu chính
                        modifier = Modifier.size(18.dp) // Tăng kích thước
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Add a Workspace",
                    fontSize = 16.sp, // Tăng font size
                    fontWeight = FontWeight.Medium, // Đổi thành Medium
                    color = TextDark // Sử dụng màu đậm hơn
                )
            }

            // Workspace list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 400.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(4) {
                    WorkspaceItem()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Spacer để thêm padding ở dưới cùng
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Chỉnh sửa WorkspaceItem để nổi bật text hơn
@Composable
fun WorkspaceItem() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Workspace selection action */ }
    ) {
        // Workspace avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TeamNexusPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LM",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp // Tăng font size
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Workspace details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Les Misérables",
                fontSize = 16.sp, // Tăng font size
                fontWeight = FontWeight.Bold, // Đổi thành Bold
                color = TextDark, // Sử dụng màu đậm hơn
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Admin: Dedo Scruf",
                fontSize = 14.sp, // Tăng font size nhẹ
                fontWeight = FontWeight.Medium, // Đổi thành Medium
                color = MediumGrey, // Sử dụng màu rõ hơn
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Options menu
        IconButton(
            onClick = { /* Workspace options */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Workspace options",
                tint = TeamNexusPurple, // Đổi sang màu chính
                modifier = Modifier.size(24.dp) // Tăng kích thước
            )
        }
    }
}