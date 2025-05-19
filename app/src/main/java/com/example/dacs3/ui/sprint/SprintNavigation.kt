package com.example.dacs3.ui.sprint

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigateToSprintList(workspaceId: String) {
    this.navigate("sprint_list/$workspaceId")
}

fun NavController.navigateToCreateSprint(workspaceId: String) {
    this.navigate("create_sprint/$workspaceId")
}

fun NavController.navigateToSprintDetail(sprintId: String) {
    this.navigate("sprint_detail/$sprintId")
}

fun NavGraphBuilder.sprintScreen(
    navController: NavController
) {
    composable(
        route = "sprint_list/{workspaceId}",
        arguments = listOf(
            navArgument("workspaceId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
        SprintScreen(
            workspaceId = workspaceId,
            onNavigateBack = { navController.popBackStack() },
            onSprintSelected = { sprint ->
                navController.navigateToSprintDetail(sprint._id)
            },
            onCreateSprint = { workspaceId ->
                navController.navigateToCreateSprint(workspaceId)
            }
        )
    }
    
    composable(
        route = "create_sprint/{workspaceId}",
        arguments = listOf(
            navArgument("workspaceId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
        CreateSprintScreen(
            workspaceId = workspaceId,
            onNavigateBack = { navController.popBackStack() },
            onSprintCreated = { navController.popBackStack() }
        )
    }
    
    composable(
        route = "sprint_detail/{sprintId}",
        arguments = listOf(
            navArgument("sprintId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
        SprintDetailScreen(
            sprintId = sprintId,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}