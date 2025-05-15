package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Epic
import java.util.Date

@Entity(tableName = "epics")
data class EpicEntity(
    @PrimaryKey
    val _id: String,
    val title: String,
    val description: String?,
    val workspace_id: String,
    val created_by: String,
    val assigned_to: String?,
    val status: String,
    val priority: String,
    val start_date: Date?,
    val due_date: Date?,
    val completed_date: Date?,
    val sprint_id: String?,
    val tasks: List<String>?,
    val created_at: Date,
    val updated_at: Date
) {
    fun toEpic(): Epic {
        return Epic(
            _id = _id,
            title = title,
            description = description,
            workspace_id = workspace_id,
            created_by = created_by,
            assigned_to = assigned_to,
            status = status,
            priority = priority,
            start_date = start_date,
            due_date = due_date,
            completed_date = completed_date,
            sprint_id = sprint_id,
            tasks = tasks,
            created_at = created_at,
            updated_at = updated_at
        )
    }

    companion object {
        fun fromEpic(epic: Epic): EpicEntity {
            return EpicEntity(
                _id = epic._id,
                title = epic.title,
                description = epic.description,
                workspace_id = epic.workspace_id,
                created_by = epic.created_by,
                assigned_to = epic.assigned_to,
                status = epic.status,
                priority = epic.priority,
                start_date = epic.start_date,
                due_date = epic.due_date,
                completed_date = epic.completed_date,
                sprint_id = epic.sprint_id,
                tasks = epic.tasks,
                created_at = epic.created_at,
                updated_at = epic.updated_at
            )
        }
    }
} 