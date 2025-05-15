package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Sprint
import java.util.Date

@Entity(tableName = "sprints")
data class SprintEntity(
    @PrimaryKey
    val _id: String,
    val name: String,
    val description: String?,
    val workspace_id: String,
    val created_by: String,
    val status: String,
    val start_date: Date,
    val end_date: Date,
    val goal: String?,
    val tasks: List<String>?,
    val created_at: Date,
    val updated_at: Date
) {
    fun toSprint(): Sprint {
        return Sprint(
            _id = _id,
            name = name,
            description = description,
            workspace_id = workspace_id,
            created_by = created_by,
            status = status,
            start_date = start_date,
            end_date = end_date,
            goal = goal,
            tasks = tasks,
            created_at = created_at,
            updated_at = updated_at
        )
    }

    companion object {
        fun fromSprint(sprint: Sprint): SprintEntity {
            return SprintEntity(
                _id = sprint._id,
                name = sprint.name,
                description = sprint.description,
                workspace_id = sprint.workspace_id,
                created_by = sprint.created_by,
                status = sprint.status,
                start_date = sprint.start_date,
                end_date = sprint.end_date,
                goal = sprint.goal,
                tasks = sprint.tasks,
                created_at = sprint.created_at,
                updated_at = sprint.updated_at
            )
        }
    }
} 