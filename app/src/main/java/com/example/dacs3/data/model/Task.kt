package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Task(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("workspace_id")
    val workspaceId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("assigned_to")
    val assignedTo: List<String>? = null,
    
    @SerializedName("created_by")
    val createdBy: String,
    
    @SerializedName("due_date")
    val dueDate: Date? = null,
    
    @SerializedName("start_date")
    val startDate: Date? = null,
    
    @SerializedName("status")
    val status: String = "To Do",
    
    @SerializedName("progress")
    val progress: Int = 0,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Date? = null
) 