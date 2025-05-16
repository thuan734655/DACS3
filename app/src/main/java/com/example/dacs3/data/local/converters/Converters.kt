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
        
        // Create a custom Gson instance that can properly deserialize WorkspaceMember objects
        val customGson = Gson().newBuilder()
            .registerTypeAdapter(object : TypeToken<List<WorkspaceMember>>() {}.type, object : com.google.gson.JsonDeserializer<List<WorkspaceMember>> {
                override fun deserialize(json: com.google.gson.JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): List<WorkspaceMember> {
                    val result = mutableListOf<WorkspaceMember>()
                    val jsonArray = json.asJsonArray
                    
                    for (element in jsonArray) {
                        val jsonObject = element.asJsonObject
                        val roleElement = jsonObject.get("role")
                        val idElement = jsonObject.get("_id")
                        val userIdElement = jsonObject.get("user_id")
                        
                        // Handle user_id that can be either a string or a User object
                        val userId: User = if (userIdElement.isJsonObject) {
                            val userObject = userIdElement.asJsonObject
                            val userId = userObject.get("_id").asString
                            val userName = userObject.get("name")?.asString ?: "Unknown"
                            val userAvatar = userObject.get("avatar")?.asString
                            val userCreatedAt = if (userObject.has("created_at")) {
                                try {
                                    val timestamp = userObject.get("created_at").asString
                                    val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
                                    simpleDateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                    simpleDateFormat.parse(timestamp)
                                } catch (e: Exception) {
                                    Date()
                                }
                            } else {
                                Date()
                            }
                            
                            User(userId, userName, userAvatar, userCreatedAt)
                        } else {
                            // Fallback for legacy data that might have user_id as a string
                            val userId = userIdElement.asString
                            User(userId, "Unknown", null, Date())
                        }
                        
                        val role = roleElement?.asString ?: "Member"
                        val id = idElement?.asString
                        
                        result.add(WorkspaceMember(userId, role, id))
                    }
                    
                    return result
                }
            })
            .create()
            
        return customGson.fromJson(value, listType)
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