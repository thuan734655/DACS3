package com.example.dacs3.ui.workspace

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigateToWorkspaceDetail(workspaceId: String) {
    this.navigate("workspace_detail/$workspaceId")
}

fun NavGraphBuilder.workspaceDetailScreen(navController: NavController) {
    composable(
        route = "workspace_detail/{workspaceId}",
        arguments = listOf(
            navArgument("workspaceId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
        WorkspaceDetailScreen(
            workspaceId = workspaceId,
            navController = navController
        )
    }
}
