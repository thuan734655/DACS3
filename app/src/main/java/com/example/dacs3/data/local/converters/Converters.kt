package com.example.dacs3.data.local.converters

import androidx.room.TypeConverter
import com.example.dacs3.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters cho Room Database
 */
class Converters {
    private val gson = Gson()

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // User converters
    @TypeConverter
    fun fromUser(user: User?): String {
        return gson.toJson(user)
    }

    @TypeConverter
    fun toUser(value: String): User? {
        return gson.fromJson(value, User::class.java)
    }

    // String List converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Comment converters
    @TypeConverter
    fun fromCommentList(value: List<Comment>?): String {
        return gson.toJson(value ?: emptyList<Comment>())
    }

    @TypeConverter
    fun toCommentList(value: String): List<Comment>? {
        val listType = object : TypeToken<List<Comment>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Attachment converters
    @TypeConverter
    fun fromAttachmentList(value: List<Attachment>?): String {
        return gson.toJson(value ?: emptyList<Attachment>())
    }

    @TypeConverter
    fun toAttachmentList(value: String): List<Attachment>? {
        val listType = object : TypeToken<List<Attachment>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ChannelMember converters
    @TypeConverter
    fun fromChannelMemberList(value: List<ChannelMember>?): String {
        return gson.toJson(value ?: emptyList<ChannelMember>())
    }

    @TypeConverter
    fun toChannelMemberList(value: String): List<ChannelMember>? {
        val listType = object : TypeToken<List<ChannelMember>>() {}.type
        return gson.fromJson(value, listType)
    }

    // WorkspaceMember converters
    @TypeConverter
    fun fromWorkspaceMemberList(value: List<WorkspaceMember>?): String {
        return gson.toJson(value ?: emptyList<WorkspaceMember>())
    }

    @TypeConverter
    fun toWorkspaceMemberList(value: String): List<WorkspaceMember>? {
        val listType = object : TypeToken<List<WorkspaceMember>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ReportTask converters
    @TypeConverter
    fun fromReportTaskList(value: List<ReportTask>?): String {
        return gson.toJson(value ?: emptyList<ReportTask>())
    }

    @TypeConverter
    fun toReportTaskList(value: String): List<ReportTask>? {
        val listType = object : TypeToken<List<ReportTask>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ReportInProgressTask converters
    @TypeConverter
    fun fromReportInProgressTaskList(value: List<ReportInProgressTask>?): String {
        return gson.toJson(value ?: emptyList<ReportInProgressTask>())
    }

    @TypeConverter
    fun toReportInProgressTaskList(value: String): List<ReportInProgressTask>? {
        val listType = object : TypeToken<List<ReportInProgressTask>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ReportPlannedTask converters
    @TypeConverter
    fun fromReportPlannedTaskList(value: List<ReportPlannedTask>?): String {
        return gson.toJson(value ?: emptyList<ReportPlannedTask>())
    }

    @TypeConverter
    fun toReportPlannedTaskList(value: String): List<ReportPlannedTask>? {
        val listType = object : TypeToken<List<ReportPlannedTask>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ReportIssue converters
    @TypeConverter
    fun fromReportIssueList(value: List<ReportIssue>?): String {
        return gson.toJson(value ?: emptyList<ReportIssue>())
    }

    @TypeConverter
    fun toReportIssueList(value: String): List<ReportIssue>? {
        val listType = object : TypeToken<List<ReportIssue>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Number converter
    @TypeConverter
    fun fromNumber(value: Number?): Double? {
        return value?.toDouble()
    }

    @TypeConverter
    fun toNumber(value: Double?): Number? {
        return value
    }
} 