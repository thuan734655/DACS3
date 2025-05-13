package com.example.dacs3.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.R
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.navigation.Screen
import com.example.dacs3.ui.theme.TeamNexusPurple
import com.example.dacs3.ui.workspaces.create.CreateWorkspaceDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val homeState by homeViewModel.homeState.collectAsState()
    val selectedTab = remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // State for the workspace creation dialog
    var showCreateWorkspaceDialog by remember { mutableStateOf(false) }
    
    // Show Create Workspace Dialog when needed
    CreateWorkspaceDialog(
        showDialog = showCreateWorkspaceDialog,
        onDismiss = { showCreateWorkspaceDialog = false },
        onWorkspaceCreated = {
            // Refresh workspaces after creating a new one
            homeViewModel.loadWorkspaces()
        }
    )
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerContentColor = Color.Black,
            ) {
                SidebarContent(
                    currentWorkspace = homeState.currentWorkspace,
                    workspaces = homeState.workspaces,
                    onWorkspaceSelected = { workspaceId ->
                        homeViewModel.switchWorkspace(workspaceId)
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onCreateWorkspace = {
                        // Show the dialog instead of navigating
                        showCreateWorkspaceDialog = true
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
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
                    activityTitle, notificationContent) = createRefs()
                
                // Header with workspace name and user
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(TeamNexusPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = homeState.currentWorkspace?.createdBy?.name?.take(1) ?: "U",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = "Hello ${homeState.currentWorkspace?.createdBy?.name ?: "User"}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val workspaceName = homeState.currentWorkspace?.name ?: "Your Workspace"
                                
                                // Sử dụng key để buộc UI làm mới khi workspace thay đổi và hiển thị trạng thái loading
                                key(workspaceName) {
                                    if (homeState.isLoading) {
                                        // Hiển thị indicator loading khi đang tải
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = TeamNexusPurple,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Loading workspace...",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.Gray
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = workspaceName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.animateContentSize(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        )
                                    }
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Workspace",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    
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
                
                // Channels section
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
                
                LazyColumn(
                    modifier = Modifier.constrainAs(channels) {
                        top.linkTo(channelsTitle.bottom, margin = 4.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                ) {
                    val channelList = homeState.channels
                    if (channelList.isNotEmpty()) {
                        items(channelList) { channel ->
                            ChannelItem(name = channel.name)
                        }
                    } else {
                        item {
                            Text(
                                text = "No channels",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
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
                
                // Unreads section
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
                
                LazyColumn(
                    modifier = Modifier.constrainAs(unreads) {
                        top.linkTo(unreadsTitle.bottom, margin = 4.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                ) {
                    val unreadChannels = homeState.unreadChannels
                    if (unreadChannels.isNotEmpty()) {
                        items(unreadChannels) { channel ->
                            ChannelItem(name = channel.name)
                        }
                    } else {
                        item {
                            Text(
                                text = "No unread messages",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                
                // Add channel button
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
                
                // Activity stream section
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
                        .constrainAs(notificationContent) {
                            top.linkTo(activityTitle.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (homeState.notifications.isEmpty()) {
                        Column(
                            modifier = Modifier.padding(top = 100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No notification!",
                                color = Color.Gray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    } else {
                        LazyColumn {
                            items(homeState.notifications) { notification ->
                                Text(
                                    text = notification.content,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    )
                                )
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarContent(
    currentWorkspace: Workspace?,
    workspaces: List<Workspace>,
    onWorkspaceSelected: (String) -> Unit,
    onCreateWorkspace: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Your Workspaces",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Divider(color = Color.LightGray.copy(alpha = 0.7f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (workspaces.isNotEmpty()) {
                items(workspaces) { workspace ->
                    WorkspaceItem(
                        workspace = workspace,
                        isSelected = workspace.id == currentWorkspace?.id,
                        onClick = { onWorkspaceSelected(workspace.id ?: "") }
                    )
                }
            } else {
                item {
                    Text(
                        text = "No workspaces found",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
        
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.LightGray.copy(alpha = 0.7f),
            thickness = 1.dp
        )
        
        Button(
            onClick = onCreateWorkspace,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = TeamNexusPurple
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Workspace",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Create Workspace")
        }
    }
}

@Composable
fun WorkspaceItem(
    workspace: Workspace,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) TeamNexusPurple.copy(alpha = 0.1f) else Color.White)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Go to ${workspace.name}",
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TeamNexusPurple else Color.Black
            )
            
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = if (isSelected) TeamNexusPurple else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
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