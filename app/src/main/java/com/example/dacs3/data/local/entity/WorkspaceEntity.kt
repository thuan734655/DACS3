package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.model.WorkspaceMember
import java.util.Date

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey
    val _id: String,
    val name: String,
    val description: String?,
    val createdById: String,
    val createdByName: String,
    val created_at: Date,
    val members: List<WorkspaceMember>?,
    val channels: List<String>?
) {
    fun toWorkspace(): Workspace {
        return Workspace(
            _id = _id,
            name = name,
            description = description,
            created_by = com.example.dacs3.data.model.User(
                _id = createdById,
                name = createdByName,
                avatar = null,
                created_at = created_at
            ),
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
                createdById = workspace.created_by._id,
                createdByName = workspace.created_by.name,
                created_at = workspace.created_at,
                members = workspace.members,
                channels = workspace.channels
            )
        }
    }
} 