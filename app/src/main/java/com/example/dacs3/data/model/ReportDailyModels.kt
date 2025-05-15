package com.example.dacs3.data.model

import java.util.Date
import com.example.dacs3.data.model.MessageResponse

// Models
data class ReportDaily(
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
)

data class ReportTask(
    val task_id: String,
    val notes: String?,
    val spent_hours: Number
)

data class ReportInProgressTask(
    val task_id: String,
    val notes: String?,
    val spent_hours: Number,
    val progress_percentage: Number
)

data class ReportPlannedTask(
    val task_id: String,
    val notes: String?,
    val estimated_hours: Number
)

data class ReportIssue(
    val description: String,
    val is_blocking: Boolean
)

// Requests
data class CreateOrUpdateReportRequest(
    val workspace_id: String,
    val date: Date?,
    val completed_tasks: List<ReportTask>?,
    val in_progress_tasks: List<ReportInProgressTask>?,
    val planned_tasks: List<ReportPlannedTask>?,
    val issues: List<ReportIssue>?,
    val general_notes: String?
)

// Responses
data class ReportDailyResponse(
    val success: Boolean,
    val data: ReportDaily?
)

data class ReportDailyListResponse(
    val success: Boolean,
    val count: Int,
    val total: Int,
    val data: List<ReportDaily>
)

data class ReportsByDateResponse(
    val success: Boolean,
    val count: Int,
    val data: List<ReportDaily>,
    val date: String
)

// MessageResponse is imported from CommonModels.kt 