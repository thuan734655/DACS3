package com.example.dacs3.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Workspace(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("created_by")
    val createdBy: String,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("members")
    val members: List<WorkspaceMember>? = null,
    
    @SerializedName("channels")
    val channels: List<String>? = null
)

data class WorkspaceMember(
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("role")
    val role: String
) 