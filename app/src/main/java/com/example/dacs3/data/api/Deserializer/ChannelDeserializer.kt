package com.example.dacs3.data.api.deserializer

import com.example.dacs3.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class ChannelDeserializer : JsonDeserializer<List<Channel>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<Channel> {
        val jsonObject = json.asJsonObject
        if (!jsonObject.has("data")) return emptyList()
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        
        val channelsArray = jsonObject.get("data").asJsonArray
        return channelsArray.map { channelElement ->
            val channel = channelElement.asJsonObject
            
            // Parse workspace
            val workspaceObj = channel.get("workspace_id").asJsonObject
            val workspace = parseWorkspace(workspaceObj, dateFormat)
            
            // Parse created_by user
            val createdBy = if (channel.has("created_by") && !channel.get("created_by").isJsonNull) {
                val createdByObj = channel.get("created_by").asJsonObject
                User(
                    _id = createdByObj.get("_id").asString,
                    name = createdByObj.get("name").asString,
                    avatar = if (createdByObj.has("avatar")) createdByObj.get("avatar").asString else null,
                    created_at = parseDate(createdByObj.get("created_at"), dateFormat) ?: Date()
                )
            } else {
                User(
                    _id = "",
                    name = "Unknown",
                    avatar = null,
                    created_at = Date()
                )
            }
            
            // Parse members
            val members = if (channel.has("members") && !channel.get("members").isJsonNull) {
                val membersArray = channel.get("members").asJsonArray
                membersArray.map { memberElement ->
                    val memberObj = memberElement.asJsonObject
                    ChannelMember(
                        user = if (memberObj.has("user") && !memberObj.get("user").isJsonNull) {
                            val userObj = memberObj.get("user").asJsonObject
                            User(
                                _id = userObj.get("_id").asString,
                                name = userObj.get("name").asString,
                                avatar = if (userObj.has("avatar")) userObj.get("avatar").asString else null,
                                created_at = parseDate(userObj.get("created_at"), dateFormat) ?: Date()
                            )
                        } else {
                            User(
                                _id = memberObj.get("user_id").asString,
                                name = "Unknown",
                                avatar = null,
                                created_at = Date()
                            )
                        },
                        last_read = parseDate(memberObj.get("last_read"), dateFormat),
                        joined_at = parseDate(memberObj.get("joined_at"), dateFormat) ?: Date(),
                        _id = memberObj.get("_id").asString
                    )
                }
            } else emptyList()

            Channel(
                _id = channel.get("_id").asString,
                name = channel.get("name").asString,
                description = if (channel.has("description")) channel.get("description").asString else null,
                workspace_id = workspace,
                created_by = createdBy,
                is_private = channel.get("is_private").asBoolean,
                members = members,
                last_message_id = if (channel.has("last_message_id")) channel.get("last_message_id").asString else null,
                last_message_preview = if (channel.has("last_message_preview")) channel.get("last_message_preview").asString else null,
                last_message_at = parseDate(channel.get("last_message_at"), dateFormat),
                created_at = parseDate(channel.get("created_at"), dateFormat) ?: Date(),
                updated_at = parseDate(channel.get("updated_at"), dateFormat) ?: Date()
            )
        }
    }

    private fun parseWorkspace(workspaceObj: JsonObject, dateFormat: SimpleDateFormat): Workspace {
        val createdBy = if (workspaceObj.has("created_by")) {
            val createdByElement = workspaceObj.get("created_by")
            if (createdByElement.isJsonObject) {
                val userObj = createdByElement.asJsonObject
                User(
                    _id = userObj.get("_id").asString,
                    name = userObj.get("name").asString,
                    avatar = if (userObj.has("avatar")) userObj.get("avatar").asString else null,
                    created_at = parseDate(userObj.get("created_at"), dateFormat) ?: Date()
                )
            } else {
                User(
                    _id = createdByElement.asString,
                    name = "Unknown",
                    avatar = null,
                    created_at = Date()
                )
            }
        } else null

        val members = if (workspaceObj.has("members")) {
            val membersArray = workspaceObj.get("members").asJsonArray
            membersArray.map { memberElement ->
                val memberObj = memberElement.asJsonObject
                WorkspaceMember(
                    user_id = User(
                        _id = memberObj.get("user_id").asString,
                        name = "Unknown",
                        avatar = null,
                        created_at = Date()
                    ),
                    role = memberObj.get("role").asString,
                    _id = memberObj.get("_id").asString
                )
            }
        } else emptyList()

        return Workspace(
            _id = workspaceObj.get("_id").asString,
            name = workspaceObj.get("name").asString,
            description = if (workspaceObj.has("description")) 
                workspaceObj.get("description").asString else null,
            created_by = createdBy ?: User(
                _id = "",
                name = "Unknown",
                avatar = null,
                created_at = Date()
            ),
            members = members,
            channels = if (workspaceObj.has("channels")) {
                workspaceObj.get("channels").asJsonArray.map { it.asString }
            } else emptyList(),
            created_at = parseDate(workspaceObj.get("created_at"), dateFormat) ?: Date()
        )
    }

    private fun parseDate(element: JsonElement?, dateFormat: SimpleDateFormat): Date? {
        if (element == null || element.isJsonNull) return null

        return try {
            dateFormat.parse(element.asString)
        } catch (e: Exception) {
            try {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(element.asString)
            } catch (e: Exception) {
                null
            }
        }
    }
}