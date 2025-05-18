package com.example.dacs3.data.local.converters

import androidx.room.TypeConverter
import com.example.dacs3.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    // --- Date ---
    @TypeConverter fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @TypeConverter fun dateToTimestamp(date: Date?): Long? = date?.time

    // --- List<String> ---
    @TypeConverter fun fromStringList(value: String?): List<String>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<String>>(){}.type) }
    @TypeConverter fun toStringList(list: List<String>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<WorkspaceMember> ---
    @TypeConverter fun fromWorkspaceMemberList(value: String?): List<WorkspaceMember>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<WorkspaceMember>>(){}.type) }
    @TypeConverter fun toWorkspaceMemberList(list: List<WorkspaceMember>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<Comment> ---
    @TypeConverter fun fromCommentList(value: String?): List<Comment>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<Comment>>(){}.type) }
    @TypeConverter fun toCommentList(list: List<Comment>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<Attachment> ---
    @TypeConverter fun fromAttachmentList(value: String?): List<Attachment>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<Attachment>>(){}.type) }
    @TypeConverter fun toAttachmentList(list: List<Attachment>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<ChannelMember> ---
    @TypeConverter fun fromChannelMemberList(value: String?): List<ChannelMember>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<ChannelMember>>(){}.type) }
    @TypeConverter fun toChannelMemberList(list: List<ChannelMember>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<ReportTask> ---
    @TypeConverter fun fromReportTaskList(value: String?): List<ReportTask>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<ReportTask>>(){}.type) }
    @TypeConverter fun toReportTaskList(list: List<ReportTask>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<ReportInProgressTask> ---
    @TypeConverter fun fromInProgressTaskList(value: String?): List<ReportInProgressTask>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<ReportInProgressTask>>(){}.type) }
    @TypeConverter fun toInProgressTaskList(list: List<ReportInProgressTask>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<ReportPlannedTask> ---
    @TypeConverter fun fromPlannedTaskList(value: String?): List<ReportPlannedTask>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<ReportPlannedTask>>(){}.type) }
    @TypeConverter fun toPlannedTaskList(list: List<ReportPlannedTask>?): String? =
        list?.let { gson.toJson(it) }

    // --- List<ReportIssue> ---
    @TypeConverter fun fromIssueList(value: String?): List<ReportIssue>? =
        value?.let { gson.fromJson(it, object: TypeToken<List<ReportIssue>>(){}.type) }
    @TypeConverter fun toIssueList(list: List<ReportIssue>?): String? =
        list?.let { gson.toJson(it) }

    // --- Number <-> Double ---
    @TypeConverter
    fun fromNumber(value: Number?): Double? = value?.toDouble()
    @TypeConverter
    fun toNumber(value: Double?): Number? = value
}
