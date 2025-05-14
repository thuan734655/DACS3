package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Sprint(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("workspace_is")
    val workspaceId: String,
    
    @SerializedName("create_by")
    val createdBy: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("goal")
    val goal: String? = null,
    
    @SerializedName("start_date")
    val startDate: Date,
    
    @SerializedName("end_date")
    val endDate: Date,
    
    @SerializedName("task")
    val tasks: List<SprintTask>? = null,
    
    @SerializedName("status")
    val status: String = "To Do",
    
    @SerializedName("progress")
    val progress: Int = 0
)

data class SprintTask(
    @SerializedName("task_id")
    val taskId: String,
    
    @SerializedName("status")
    val status: String
) 