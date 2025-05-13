package com.example.dacs3.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.dacs3.R
import com.example.dacs3.ui.theme.TeamNexusPurple

@Composable
fun HomeScreen(
    navController: NavController
) {
    val selectedTab = remember { mutableStateOf(0) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNav(
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
        ) {
            val (header, channelsTitle, channels, divider1, 
                unreadsTitle, unreads, addChannel, divider2, 
                activityTitle, emptyNotification) = createRefs()
            
            Row(
                modifier = Modifier
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Workspace",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            SectionHeader(
                title = "Channels",
                hasDropdown = true,
                modifier = Modifier.constrainAs(channelsTitle) {
                    top.linkTo(header.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            )
            
            Column(
                modifier = Modifier.constrainAs(channels) {
                    top.linkTo(channelsTitle.bottom, margin = 4.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                repeat(3) {
                    ChannelItem(name = "abc-xyz")
                }
            }
            
            Divider(
                modifier = Modifier.constrainAs(divider1) {
                    top.linkTo(channels.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            
            SectionHeader(
                title = "Unreads",
                hasDropdown = false,
                modifier = Modifier.constrainAs(unreadsTitle) {
                    top.linkTo(divider1.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            )
            
            Column(
                modifier = Modifier.constrainAs(unreads) {
                    top.linkTo(unreadsTitle.bottom, margin = 4.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                ChannelItem(name = "abc-xyz")
            }
            
            Row(
                modifier = Modifier
                    .constrainAs(addChannel) {
                        top.linkTo(unreads.bottom, margin = 4.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .clickable { }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Channel",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add channel",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Divider(
                modifier = Modifier.constrainAs(divider2) {
                    top.linkTo(addChannel.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            
            SectionHeader(
                title = "Activity stream",
                hasDropdown = true,
                modifier = Modifier.constrainAs(activityTitle) {
                    top.linkTo(divider2.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            )
            
            Box(
                modifier = Modifier
                    .constrainAs(emptyNotification) {
                        top.linkTo(activityTitle.bottom, margin = 140.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notification!",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    hasDropdown: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        if (hasDropdown) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ChannelItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}

@Composable
fun BottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column {
        Divider(
            color = Color.LightGray.copy(alpha = 0.5f),
            thickness = 1.dp
        )
        
        Row(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavIcon(
                icon = Icons.Filled.Home,
                label = "Home",
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                tint = if (selectedTab == 0) TeamNexusPurple else Color.Gray
            )
            
            BottomNavIcon(
                icon = Icons.AutoMirrored.Filled.Message, 
                label = "Chat",
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            
            BottomNavIcon(
                icon = Icons.Filled.GridView,
                label = "Workspace",
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            
            BottomNavIcon(
                icon = Icons.Filled.Person,
                label = "Profile",
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
            
            BottomNavIcon(
                icon = Icons.Filled.MoreVert,
                label = "More",
                selected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
fun BottomNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tint: Color = Color.Gray
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
} 