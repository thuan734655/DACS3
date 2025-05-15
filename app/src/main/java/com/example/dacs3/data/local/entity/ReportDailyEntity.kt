package com.example.dacs3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dacs3.data.model.ReportDaily
import com.example.dacs3.data.model.ReportInProgressTask
import com.example.dacs3.data.model.ReportIssue
import com.example.dacs3.data.model.ReportPlannedTask
import com.example.dacs3.data.model.ReportTask
import java.util.Date

@Entity(tableName = "reports_daily")
data class ReportDailyEntity(
    @PrimaryKey
    val _id: String,
    val user_id: String,
    val workspace_id: String,
    val date: Date,
    val completed_tasks: List<ReportTask>?,
    val in_progress_tasks: List<ReportInProgressTask>?,
    val planned_tasks: List<ReportPlannedTask>?,
    val issues: List<ReportIssue>?,
    val general_notes: String?,
    val created_at: Date,
    val updated_at: Date
) {
    fun toReportDaily(): ReportDaily {
        return ReportDaily(
            _id = _id,
            user_id = user_id,
            workspace_id = workspace_id,
            date = date,
            completed_tasks = completed_tasks,
            in_progress_tasks = in_progress_tasks,
            planned_tasks = planned_tasks,
            issues = issues,
            general_notes = general_notes,
            created_at = created_at,
            updated_at = updated_at
        )
    }

    companion object {
        fun fromReportDaily(reportDaily: ReportDaily): ReportDailyEntity {
            return ReportDailyEntity(
                _id = reportDaily._id,
                user_id = reportDaily.user_id,
                workspace_id = reportDaily.workspace_id,
                date = reportDaily.date,
                completed_tasks = reportDaily.completed_tasks,
                in_progress_tasks = reportDaily.in_progress_tasks,
                planned_tasks = reportDaily.planned_tasks,
                issues = reportDaily.issues,
                general_notes = reportDaily.general_notes,
                created_at = reportDaily.created_at,
                updated_at = reportDaily.updated_at
            )
        }
    }
} 