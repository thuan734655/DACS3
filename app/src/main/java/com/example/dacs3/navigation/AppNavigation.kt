package com.example.dacs3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dacs3.ui.auth.AuthViewModel
import com.example.dacs3.ui.auth.LoginScreen
import com.example.dacs3.ui.auth.RegisterScreen
import com.example.dacs3.ui.bugs.BugDetailScreen
import com.example.dacs3.ui.bugs.BugListScreen
import com.example.dacs3.ui.bugs.CreateBugScreen
import com.example.dacs3.ui.channel.ChannelDetailScreen
import com.example.dacs3.ui.channel.ChannelListScreen
import com.example.dacs3.ui.channel.CreateChannelScreen
import com.example.dacs3.ui.direct.DirectMessageScreen
import com.example.dacs3.ui.epic.CreateEpicScreen
import com.example.dacs3.ui.epic.EpicDetailScreen
import com.example.dacs3.ui.epic.EpicListScreen
import com.example.dacs3.ui.home.HomeScreen
import com.example.dacs3.ui.kanban.KanbanScreen
import com.example.dacs3.ui.notification.NotificationScreen
import com.example.dacs3.ui.profile.ProfileScreen
import com.example.dacs3.ui.tasks.CreateTaskScreen
import com.example.dacs3.ui.tasks.TaskDetailScreen
import com.example.dacs3.ui.tasks.TaskListScreen
import com.example.dacs3.ui.welcome.WelcomeScreen
import com.example.dacs3.ui.workspace.CreateWorkspaceScreen
import com.example.dacs3.ui.workspace.WorkspaceDetailScreen
import com.example.dacs3.ui.workspace.WorkspaceListScreen

@Composable
fun AppNavigation(
    navController: NavHostController, 
    startDestination: String = "welcome",
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Always start with welcome or login, never directly to home
    // Remove automatic home navigation based on session
    
    // Get current user ID from AuthViewModel instead of hardcoding
    val currentUserId by authViewModel.currentUserId.collectAsState()
    
    NavHost(navController = navController, startDestination = startDestination) {
        // Home screen
        composable("home") {
            HomeScreen(
                userId = currentUserId,
                onNavigateToWorkspaces = { navController.navigate("workspaces") },
                onNavigateToDirectMessage = { targetUserId -> navController.navigate("direct_message/$targetUserId") },
                onNavigateToChannel = { channelId -> navController.navigate("channel/$channelId") },
                onNavigateToTask = { taskId -> navController.navigate("task/$taskId") },
                onNavigateToKanban = { navController.navigate("kanban") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") }
            )
        }
        
        composable("welcome") {
            WelcomeScreen(navController)
        }
        
        composable("login") {
            LoginScreen(navController)
        }
        
        composable("register") {
            RegisterScreen(navController)
        }
        
        // Workspace related screens
        composable("workspaces") {
            WorkspaceListScreen(
                onWorkspaceClick = { workspaceId -> navController.navigate("workspace/$workspaceId") },
                onCreateWorkspace = { navController.navigate("create_workspace") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("create_workspace") {
            CreateWorkspaceScreen(
                onWorkspaceCreated = { workspaceId ->
                    navController.navigate("workspace/$workspaceId") {
                        popUpTo("workspaces")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "workspace/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            WorkspaceDetailScreen(
                workspaceId = workspaceId,
                onNavigateToChannel = { channelId -> navController.navigate("channel/$channelId") },
                onNavigateToEpics = { navController.navigate("epics/$workspaceId") },
                onCreateChannel = { navController.navigate("create_channel/$workspaceId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Channel related screens
        composable("channels") {
            ChannelListScreen(
                onChannelClick = { channelId -> navController.navigate("channel/$channelId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "create_channel/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            CreateChannelScreen(
                workspaceId = workspaceId,
                onChannelCreated = { channelId ->
                    navController.navigate("channel/$channelId") {
                        popUpTo("workspace/$workspaceId")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "channel/{channelId}",
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
            ChannelDetailScreen(
                channelId = channelId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Direct messaging with actual current user
        composable(
            route = "direct_message/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val targetUserId = backStackEntry.arguments?.getString("userId") ?: ""
            DirectMessageScreen(
                userId = targetUserId,
                currentUserId = currentUserId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Epic related screens
        composable(
            route = "epics/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            EpicListScreen(
                workspaceId = workspaceId,
                onEpicClick = { epicId -> navController.navigate("epic/$epicId") },
                onCreateEpic = { navController.navigate("create_epic/$workspaceId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "create_epic/{workspaceId}",
            arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
            CreateEpicScreen(
                onEpicCreated = { epicId ->
                    navController.navigate("epic/$epicId") {
                        popUpTo("epics/$workspaceId")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "epic/{epicId}",
            arguments = listOf(navArgument("epicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val epicId = backStackEntry.arguments?.getString("epicId") ?: ""
            EpicDetailScreen(
                epicId = epicId,
                onNavigateToTasks = { navController.navigate("tasks/$epicId") },
                onCreateTask = { navController.navigate("create_task/$epicId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Task related screens
        composable(
            route = "tasks/{epicId}",
            arguments = listOf(navArgument("epicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val epicId = backStackEntry.arguments?.getString("epicId") ?: ""
            TaskListScreen(
                epicId = epicId,
                onTaskClick = { taskId -> navController.navigate("task/$taskId") },
                onCreateTask = { navController.navigate("create_task/$epicId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "create_task/{epicId}",
            arguments = listOf(navArgument("epicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val epicId = backStackEntry.arguments?.getString("epicId") ?: ""
            CreateTaskScreen(
                epicId = epicId,
                onTaskCreated = { taskId ->
                    navController.navigate("task/$taskId") {
                        popUpTo("tasks/$epicId")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "task/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                taskId = taskId,
                onNavigateToBugs = { navController.navigate("bugs/$taskId") },
                onCreateBug = { navController.navigate("create_bug/$taskId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Bug related screens
        composable(
            route = "bugs/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            BugListScreen(
                taskId = taskId,
                onBugClick = { bugId -> navController.navigate("bug/$bugId") },
                onCreateBug = { navController.navigate("create_bug/$taskId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "create_bug/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            CreateBugScreen(
                taskId = taskId,
                onBugCreated = {
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "bug/{bugId}",
            arguments = listOf(navArgument("bugId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bugId = backStackEntry.arguments?.getString("bugId") ?: ""
            BugDetailScreen(
                bugId = bugId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Kanban board
        composable("kanban") {
            KanbanScreen(
                onNavigateToTask = { taskId -> navController.navigate("task/$taskId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Profile screen with actual user ID
        composable("profile") {
            ProfileScreen(
                userId = currentUserId,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        
        // Notification screen
        composable("notifications") {
            NotificationScreen(
                userId = currentUserId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 