package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.Bug
import com.example.dacs3.data.model.Comment
import java.util.Date

@Entity(tableName = "bugs")
data class BugEntity(
    @PrimaryKey
    val _id: String,
    val title: String,
//    val description: String?,
//    val workspace_id: String,
//    val task_id: String?,
//    val reported_by: String,
//    val assigned_to: String?,
//    val status: String,
//    val completed_date: Date?,
//    val priority: String,
//    val comments: List<Comment>?,
//    val created_at: Date,
//    val updated_at: Date
) {
//    fun toBug(): Bug {
//        return Bug(
//            _id = _id,
//            title = title,
//            description = description,
//            workspace_id = workspace_id,
//            task_id = task_id,
//            reported_by = reported_by,
//            assigned_to = assigned_to,
//            status = status,
//            completed_date = completed_date,
//            priority = priority,
//            comments = comments,
//            created_at = created_at,
//            updated_at = updated_at
//        )
//    }
//
//    companion object {
//        fun fromBug(bug: Bug): BugEntity {
//            return BugEntity(
//                _id = bug._id,
//                title = bug.title,
//                description = bug.description,
//                workspace_id = bug.workspace_id,
//                task_id = bug.task_id,
//                reported_by = bug.reported_by,
//                assigned_to = bug.assigned_to,
//                status = bug.status,
//                completed_date = bug.completed_date,
//                priority = bug.priority,
//                comments = bug.comments,
//                created_at = bug.created_at,
//                updated_at = bug.updated_at
//            )
//        }
//    }
} 