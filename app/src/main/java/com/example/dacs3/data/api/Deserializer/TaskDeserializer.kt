package com.example.dacs3.data.api.deserializer

import com.example.dacs3.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class TaskDeserializer : JsonDeserializer<Task> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Task {
        val jsonObject = if (json.asJsonObject.has("data")) {
            json.asJsonObject.get("data").asJsonObject
        } else {
            json.asJsonObject
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse workspace_id as WorkspaceInfo
        val workspaceIdElement = jsonObject.get("workspace_id")
        val workspaceInfo = if (workspaceIdElement != null && !workspaceIdElement.isJsonNull) {
            if (workspaceIdElement.isJsonObject) {
                val workspaceObj = workspaceIdElement.asJsonObject
                WorkspaceInfo(
                    _id = workspaceObj.get("_id").asString,
                    name = workspaceObj.get("name").asString,
                    description = if (workspaceObj.has("description") && !workspaceObj.get("description").isJsonNull)
                        workspaceObj.get("description").asString else null,
                    created_by = if (workspaceObj.has("created_by")) {
                        if (workspaceObj.get("created_by").isJsonPrimitive) {
                            workspaceObj.get("created_by").asString
                        } else {
                            workspaceObj.get("created_by").asJsonObject.get("_id").asString
                        }
                    } else "",
                    members = null,
                    channels = null,
                    created_at = if (workspaceObj.has("created_at")) workspaceObj.get("created_at").asString else "",
                    __v = if (workspaceObj.has("__v")) workspaceObj.get("__v").asInt else 0
                )
            } else {
                // Fallback for string ID
                WorkspaceInfo(
                    _id = workspaceIdElement.asString,
                    name = "",
                    description = null
                )
            }
        } else {
            WorkspaceInfo(
                _id = "",  // Fallback empty workspace info
                name = "",
                description = null
            )
        }

        // Parse created_by as UserInfo
        val createdByElement = jsonObject.get("created_by")
        val createdBy = if (createdByElement.isJsonObject) {
            val userObj = createdByElement.asJsonObject
            UserInfo(
                _id = userObj.get("_id").asString,
                name = userObj.get("name").asString,
                avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull)
                    userObj.get("avatar").asString else "",
                created_at = if (userObj.has("created_at")) userObj.get("created_at").asString else "",
                __v = if (userObj.has("__v")) userObj.get("__v").asInt else 0
            )
        } else {
            // Fallback for string ID
            UserInfo(
                _id = createdByElement.asString,
                name = "Unknown",
                avatar = ""
            )
        }

        // Parse assigned_to as UserInfo, which might be null
        val assignedToElement = jsonObject.get("assigned_to")
        val assignedTo = if (assignedToElement != null && !assignedToElement.isJsonNull) {
            if (assignedToElement.isJsonObject) {
                val userObj = assignedToElement.asJsonObject
                UserInfo(
                    _id = userObj.get("_id").asString,
                    name = userObj.get("name").asString,
                    avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull)
                        userObj.get("avatar").asString else "",
                    created_at = if (userObj.has("created_at")) userObj.get("created_at").asString else "",
                    __v = if (userObj.has("__v")) userObj.get("__v").asInt else 0
                )
            } else {
                // Fallback for string ID
                UserInfo(
                    _id = assignedToElement.asString,
                    name = "Unknown",
                    avatar = ""
                )
            }
        } else {
            null
        }

        // Parse comments
        val commentsElement = jsonObject.get("comments")
        val comments = if (commentsElement != null && !commentsElement.isJsonNull) {
            val commentsList = mutableListOf<Comment>()
            val commentsArray = commentsElement.asJsonArray
            for (commentElement in commentsArray) {
                val commentObj = commentElement.asJsonObject
                // Parse user_id as UserInfo object
                val userIdElement = commentObj.get("user_id")
                val userInfo = if (userIdElement != null && !userIdElement.isJsonNull) {
                    if (userIdElement.isJsonObject) {
                        val userObj = userIdElement.asJsonObject
                        UserInfo(
                            _id = userObj.get("_id").asString,
                            name = if (userObj.has("name") && !userObj.get("name").isJsonNull) userObj.get("name").asString else "Unknown",
                            avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull) userObj.get("avatar").asString else "",
                            created_at = if (userObj.has("created_at")) userObj.get("created_at").asString else "",
                            __v = if (userObj.has("__v")) userObj.get("__v").asInt else 0
                        )
                    } else {
                        // Fallback for string ID
                        UserInfo(
                            _id = userIdElement.asString,
                            name = "Unknown",
                            avatar = ""
                        )
                    }
                } else {
                    UserInfo(
                        _id = "",
                        name = "Unknown",
                        avatar = ""
                    )
                }
                
                val comment = Comment(
                    user_id = userInfo,
                    content = commentObj.get("content").asString,
                    created_at = parseDate(commentObj.get("created_at"), dateFormat) ?: Date(),
                    updated_at = parseDate(commentObj.get("updated_at"), dateFormat) ?: Date()
                )
                commentsList.add(comment)
            }
            commentsList
        } else {
            null
        }

        // Parse attachments
        val attachmentsElement = jsonObject.get("attachments")
        val attachments = if (attachmentsElement != null && !attachmentsElement.isJsonNull) {
            val attachmentsList = mutableListOf<Attachment>()
            val attachmentsArray = attachmentsElement.asJsonArray
            for (attachmentElement in attachmentsArray) {
                val attachmentObj = attachmentElement.asJsonObject
                val attachment = Attachment(
                    file_name = attachmentObj.get("file_name").asString,
                    file_url = attachmentObj.get("file_url").asString,
                    uploaded_by = attachmentObj.get("uploaded_by").asString,
                    uploaded_at = parseDate(attachmentObj.get("uploaded_at"), dateFormat) ?: Date()
                )
                attachmentsList.add(attachment)
            }
            attachmentsList
        } else {
            null
        }

        return Task(
            _id = jsonObject.get("_id").asString,
            title = jsonObject.get("title").asString,
            description = if (jsonObject.has("description") && !jsonObject.get("description").isJsonNull)
                jsonObject.get("description").asString else null,
            workspace_id = workspaceInfo, 
            epic_id = if (jsonObject.has("epic_id") && !jsonObject.get("epic_id").isJsonNull)
                jsonObject.get("epic_id").asString else null,
            created_by = createdBy,
            assigned_to = assignedTo,
            status = jsonObject.get("status").asString,
            priority = jsonObject.get("priority").asString,
            estimated_hours = jsonObject.get("estimated_hours").asNumber,
            spent_hours = jsonObject.get("spent_hours").asNumber,
            start_date = if (jsonObject.has("start_date") && !jsonObject.get("start_date").isJsonNull)
                parseDate(jsonObject.get("start_date"), dateFormat) else null,
            due_date = if (jsonObject.has("due_date") && !jsonObject.get("due_date").isJsonNull)
                parseDate(jsonObject.get("due_date"), dateFormat) else null,
            completed_date = if (jsonObject.has("completed_date") && !jsonObject.get("completed_date").isJsonNull)
                parseDate(jsonObject.get("completed_date"), dateFormat) else null,
            sprint_id = if (jsonObject.has("sprint_id") && !jsonObject.get("sprint_id").isJsonNull)
                jsonObject.get("sprint_id").asString else null,
            comments = comments,
            attachments = attachments,
            created_at = parseDate(jsonObject.get("created_at"), dateFormat) ?: Date(),
            updated_at = parseDate(jsonObject.get("updated_at"), dateFormat) ?: Date()
        )
    }

    private fun parseDate(element: JsonElement?, dateFormat: SimpleDateFormat): Date? {
        if (element == null || element.isJsonNull) return null

        return try {
            dateFormat.parse(element.asString)
        } catch (e: Exception) {
            try {
                // Try alternate format without milliseconds
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(element.asString)
            } catch (e: Exception) {
                null
            }
        }
    }
}
