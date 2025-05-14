package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Notification(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("workspace_id")
    val workspaceId: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("related_id")
    val relatedId: String? = null,
    
    @SerializedName("is_read")
    val isRead: Boolean = false,
    
    @SerializedName("created_at")
    val createdAt: Date? = null
) 