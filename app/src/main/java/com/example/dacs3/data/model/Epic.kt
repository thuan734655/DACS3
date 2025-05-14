package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Epic(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("workspace_id")
    val workspaceId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("created_by")
    val createdBy: String,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("start_date")
    val startDate: Date? = null,
    
    @SerializedName("end_date")
    val endDate: Date? = null,
    
    @SerializedName("status")
    val status: String = "To Do",
    
    @SerializedName("progress")
    val progress: Int = 0
) 