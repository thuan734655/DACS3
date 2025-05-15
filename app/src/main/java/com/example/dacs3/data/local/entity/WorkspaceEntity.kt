package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.model.WorkspaceMember
import java.util.Date

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey
    val _id: String,
    val name: String,
    val description: String?,
    val created_by: String,
    val created_at: Date,
    val members: List<WorkspaceMember>?,
    val channels: List<String>?
) {
    fun toWorkspace(): Workspace {
        return Workspace(
            _id = _id,
            name = name,
            description = description,
            created_by = created_by,
            created_at = created_at,
            members = members,
            channels = channels
        )
    }

    companion object {
        fun fromWorkspace(workspace: Workspace): WorkspaceEntity {
            return WorkspaceEntity(
                _id = workspace._id,
                name = workspace.name,
                description = workspace.description,
                created_by = workspace.created_by,
                created_at = workspace.created_at,
                members = workspace.members,
                channels = workspace.channels
            )
        }
    }
} 