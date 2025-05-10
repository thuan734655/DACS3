package com.example.dacs3.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object WorkspaceList : Screen("workspace_list")
    object WorkspaceDetail : Screen("workspace_detail/{workspaceId}") {
        fun createRoute(workspaceId: String) = "workspace_detail/$workspaceId"
    }
    object EpicList : Screen("epic_list/{workspaceId}") {
        fun createRoute(workspaceId: String) = "epic_list/$workspaceId"
    }
    object EpicDetail : Screen("epic_detail/{epicId}") {
        fun createRoute(epicId: String) = "epic_detail/$epicId"
    }
    object CreateEpic : Screen("create_epic/{workspaceId}") {
        fun createRoute(workspaceId: String) = "create_epic/$workspaceId"
    }
    object TaskList : Screen("task_list/{epicId}") {
        fun createRoute(epicId: String) = "task_list/$epicId"
    }
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object CreateTask : Screen("create_task/{epicId}") {
        fun createRoute(epicId: String) = "create_task/$epicId"
    }
    object BugList : Screen("bug_list/{taskId}") {
        fun createRoute(taskId: String) = "bug_list/$taskId"
    }
    object CreateBug : Screen("create_bug/{taskId}") {
        fun createRoute(taskId: String) = "create_bug/$taskId"
    }
    // Add other screens as needed
} 