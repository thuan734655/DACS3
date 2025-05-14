package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DailyReport(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("workspace_id")
    val workspaceId: String,
    
    @SerializedName("date")
    val date: Date,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("inprogress")
    val inProgress: List<ReportIssue>? = null,
    
    @SerializedName("completed")
    val completed: List<ReportIssue>? = null,
    
    @SerializedName("created_at")
    val createdAt: Date? = null
)

data class ReportIssue(
    @SerializedName("issue")
    val issue: String? = null,
    
    @SerializedName("progress")
    val progress: Int = 0
) 