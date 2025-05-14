package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName

data class Dashboard(
    @SerializedName("workspaces_count")
    val workspacesCount: Int = 0,
    
    @SerializedName("tasks_count")
    val tasksCount: Int = 0,
    
    @SerializedName("tasks_completed")
    val tasksCompleted: Int = 0,
    
    @SerializedName("tasks_in_progress")
    val tasksInProgress: Int = 0,
    
    @SerializedName("recent_activities")
    val recentActivities: List<ActivityItem>? = null,
    
    @SerializedName("upcoming_deadlines")
    val upcomingDeadlines: List<Task>? = null
)

data class ActivityItem(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("timestamp")
    val timestamp: String
) 