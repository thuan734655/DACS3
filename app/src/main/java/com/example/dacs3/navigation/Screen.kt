package com.example.dacs3.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object WorkspaceList : Screen("workspaces")
    object WorkspaceDetail : Screen("workspace_detail/{workspaceId}") {
        fun createRoute(workspaceId: String) = "workspace_detail/$workspaceId"
    }
    object ConversationList : Screen("conversations") 
    object WorkspaceChat : Screen("workspace_chat/{workspaceId}") {
        fun createRoute(workspaceId: String) = "workspace_chat/$workspaceId"
    }
    // OTP verification
    object OtpVerification : Screen("otp_verification/{email}") {
        fun createRoute(email: String) = "otp_verification/$email"
    }
    // Two Factor Authentication
    object TwoFactorAuth : Screen("2fa_verification/{email}/{password}") {
        fun createRoute(email: String, password: String? = null) = 
            if (password != null) {
                "2fa_verification/$email/$password"
            } else {
                "2fa_verification/$email/null"
            }
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
    object SprintList : Screen("sprints/{workspaceId}") {
        fun createRoute(workspaceId: String) = "sprints/$workspaceId"
    }
    object SprintDetail : Screen("sprint_detail/{sprintId}") {
        fun createRoute(sprintId: String) = "sprint_detail/$sprintId"
    }
    object EditSprint : Screen("edit_sprint/{sprintId}") {
        fun createRoute(sprintId: String) = "edit_sprint/$sprintId"
    }
    object CreateSprint : Screen("create_sprint/{workspaceId}") {
        fun createRoute(workspaceId: String) = "create_sprint/$workspaceId"
    }
    object MyTasks : Screen("my_tasks")
    object DailyReport : Screen("daily_report")
    object BugList : Screen("bug_list/{taskId}") {
        fun createRoute(taskId: String) = "bug_list/$taskId"
    }
    object CreateBug : Screen("create_bug/{taskId}") {
        fun createRoute(taskId: String) = "create_bug/$taskId"
    }
    object WorkspaceMembers : Screen("workspace_members/{workspaceId}") {
        fun createRoute(workspaceId: String) = "workspace_members/$workspaceId"
    }
    object Profile : Screen("profile")
    object Notifications : Screen("notifications") 
    object DirectMessages : Screen("direct_messages")
    object DirectChat : Screen("direct_chat/{userId}") {
        fun createRoute(userId: String) = "direct_chat/$userId"
    }
    object Dashboard : Screen("dashboard")
    object Board : Screen("board")
    object SprintView : Screen("sprint_view")
    object EpicView : Screen("epic_view")
    object TaskView : Screen("task_view")
    // Add other screens as needed
}