package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Attachment
import com.example.dacs3.data.model.Comment
import com.example.dacs3.data.model.Task
import java.util.Date

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val _id: String,
    val title: String,
    val description: String?,
//    val workspace_id: String,
//    val epic_id: String?,
//    val created_by: String,
//    val assigned_to: String?,
//    val status: String,
//    val priority: String,
//    val estimated_hours: Number,
//    val spent_hours: Number,
//    val start_date: Date?,
//    val due_date: Date?,
//    val completed_date: Date?,
//    val sprint_id: String?,
//    val comments: List<Comment>?,
//    val attachments: List<Attachment>?,
//    val created_at: Date,
//    val updated_at: Date
) {
//    fun toTask(): Task {
//        return Task(
//            _id = _id,
//            title = title,
//            description = description,
//            workspace_id = workspace_id,
//            epic_id = epic_id,
//            created_by = created_by,
//            assigned_to = assigned_to,
//            status = status,
//            priority = priority,
//            estimated_hours = estimated_hours,
//            spent_hours = spent_hours,
//            start_date = start_date,
//            due_date = due_date,
//            completed_date = completed_date,
//            sprint_id = sprint_id,
//            comments = comments,
//            attachments = attachments,
//            created_at = created_at,
//            updated_at = updated_at
//        )
//    }
//
//    companion object {
//        fun fromTask(task: Task): TaskEntity {
//            return TaskEntity(
//                _id = task._id,
//                title = task.title,
//                description = task.description,
//                workspace_id = task.workspace_id,
//                epic_id = task.epic_id,
//                created_by = task.created_by,
//                assigned_to = task.assigned_to,
//                status = task.status,
//                priority = task.priority,
//                estimated_hours = task.estimated_hours,
//                spent_hours = task.spent_hours,
//                start_date = task.start_date,
//                due_date = task.due_date,
//                completed_date = task.completed_date,
//                sprint_id = task.sprint_id,
//                comments = task.comments,
//                attachments = task.attachments,
//                created_at = task.created_at,
//                updated_at = task.updated_at
//            )
//        }
//    }
} 