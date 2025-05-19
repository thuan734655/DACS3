package com.example.dacs3.data.api.deserializer

import com.example.dacs3.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class NotificationDeserializer : JsonDeserializer<Notification> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Notification {
        val jsonObject = if (json.asJsonObject.has("data")) {
            json.asJsonObject.get("data").asJsonObject
        } else {
            json.asJsonObject
        }
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse user_id field as User
        val userIdElement = jsonObject.get("user_id")
        val user = if (userIdElement.isJsonObject) {
            val userObj = userIdElement.asJsonObject
            User(
                _id = userObj.get("_id").asString,
                name = userObj.get("name").asString,
                avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull)
                    userObj.get("avatar").asString else null,
                created_at = parseDate(userObj.get("created_at"), dateFormat) ?: Date()
            )
        } else {
            // Fallback for string ID
            User(
                _id = userIdElement.asString,
                name = "Unknown",
                avatar = null,
                created_at = Date()
            )
        }

        // Parse workspace_id field as Workspace
        val workspaceIdElement = jsonObject.get("workspace_id")
        val workspace = if (workspaceIdElement.isJsonObject) {
            val workspaceObj = workspaceIdElement.asJsonObject
            
            // Handle created_by in workspace
            val createdByElement = if (workspaceObj.has("created_by")) workspaceObj.get("created_by") else null
            val createdBy = if (createdByElement != null && createdByElement.isJsonObject) {
                val createdByObj = createdByElement.asJsonObject
                User(
                    _id = createdByObj.get("_id").asString,
                    name = createdByObj.get("name").asString,
                    avatar = if (createdByObj.has("avatar") && !createdByObj.get("avatar").isJsonNull)
                        createdByObj.get("avatar").asString else null,
                    created_at = parseDate(createdByObj.get("created_at"), dateFormat) ?: Date()
                )
            } else if (createdByElement != null) {
                User(
                    _id = createdByElement.asString,
                    name = "Unknown",
                    avatar = null,
                    created_at = Date()
                )
            } else {
                User(
                    _id = "",
                    name = "Unknown",
                    avatar = null,
                    created_at = Date()
                )
            }

            // Parse members list in workspace
            val membersElement = if (workspaceObj.has("members")) workspaceObj.get("members") else null
            val members = if (membersElement != null && !membersElement.isJsonNull) {
                val membersList = mutableListOf<WorkspaceMember>()
                val membersArray = membersElement.asJsonArray

                for (memberElement in membersArray) {
                    val memberObj = memberElement.asJsonObject
                    val memberUserIdElement = memberObj.get("user_id")

                    val memberUserId = if (memberUserIdElement.isJsonObject) {
                        val userObj = memberUserIdElement.asJsonObject
                        User(
                            _id = userObj.get("_id").asString,
                            name = userObj.get("name").asString,
                            avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull)
                                userObj.get("avatar").asString else null,
                            created_at = parseDate(userObj.get("created_at"), dateFormat) ?: Date()
                        )
                    } else {
                        User(
                            _id = memberUserIdElement.asString,
                            name = "Unknown",
                            avatar = null,
                            created_at = Date()
                        )
                    }

                    val role = memberObj.get("role").asString
                    val id = if (memberObj.has("_id")) memberObj.get("_id").asString else null

                    membersList.add(WorkspaceMember(memberUserId, role, id))
                }
                membersList
            } else {
                emptyList()
            }

            // Parse channels list
            val channelsElement = if (workspaceObj.has("channels")) workspaceObj.get("channels") else null
            val channels = if (channelsElement != null && !channelsElement.isJsonNull) {
                val channelsList = mutableListOf<String>()
                val channelsArray = channelsElement.asJsonArray
                for (channelElement in channelsArray) {
                    channelsList.add(channelElement.asString)
                }
                channelsList
            } else {
                emptyList()
            }

            Workspace(
                _id = workspaceObj.get("_id").asString,
                name = workspaceObj.get("name").asString,
                description = if (workspaceObj.has("description") && !workspaceObj.get("description").isJsonNull)
                    workspaceObj.get("description").asString else null,
                created_by = createdBy,
                members = members,
                channels = channels,
                created_at = parseDate(workspaceObj.get("created_at"), dateFormat) ?: Date()
            )
        } else {
            // Fallback for string ID
            Workspace(
                _id = workspaceIdElement.asString,
                name = "Unknown Workspace",
                description = null,
                created_by = User(
                    _id = "",
                    name = "Unknown",
                    avatar = null,
                    created_at = Date()
                ),
                members = emptyList(),
                channels = emptyList(),
                created_at = Date()
            )
        }

        return Notification(
            _id = jsonObject.get("_id").asString,
            user_id = user,
            type = jsonObject.get("type").asString,
            type_id = if (jsonObject.has("type_id") && !jsonObject.get("type_id").isJsonNull)
                jsonObject.get("type_id").asString else null,
            workspace_id = workspace,
            content = jsonObject.get("content").asString,
            related_id = if (jsonObject.has("related_id") && !jsonObject.get("related_id").isJsonNull)
                jsonObject.get("related_id").asString else null,
            is_read = jsonObject.get("is_read").asBoolean,
            created_at = parseDate(jsonObject.get("created_at"), dateFormat) ?: Date()
        )
    }

    private fun parseDate(element: JsonElement?, dateFormat: SimpleDateFormat): Date? {
        return try {
            if (element != null && !element.isJsonNull) {
                dateFormat.parse(element.asString)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
