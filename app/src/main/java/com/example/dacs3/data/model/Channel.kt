package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Channel(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("workspace_id")
    val workspaceId: String,
    
    @SerializedName("created_by")
    val createdBy: String,
    
    @SerializedName("is_private")
    val isPrivate: Boolean = false,
    
    @SerializedName("members")
    val members: List<String>? = null,
    
    @SerializedName("last_message_id")
    val lastMessageId: String? = null,
    
    @SerializedName("last_message_preview")
    val lastMessagePreview: String? = null,
    
    @SerializedName("last_message_at")
    val lastMessageAt: Date? = null,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Date? = null
) 