package com.example.dacs3.data.api.deserializer

import com.example.dacs3.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class WorkspaceDeserializer : JsonDeserializer<Workspace> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Workspace {
        val jsonObject = if (json.asJsonObject.has("data")) {
            json.asJsonObject.get("data").asJsonObject
        } else {
            json.asJsonObject
        }
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse created_by field as User
        val createdByElement = jsonObject.get("created_by")
        val createdBy = if (createdByElement.isJsonObject) {
            val userObj = createdByElement.asJsonObject
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
                _id = createdByElement.asString,
                name = "Unknown",
                avatar = null,
                created_at = Date()
            )
        }

        // Parse members list
        val membersElement = jsonObject.get("members")
        val members = if (membersElement != null && !membersElement.isJsonNull) {
            val membersList = mutableListOf<WorkspaceMember>()
            val membersArray = membersElement.asJsonArray

            for (memberElement in membersArray) {
                val memberObj = memberElement.asJsonObject
                val userIdElement = memberObj.get("user_id")

                val userId = if (userIdElement.isJsonObject) {
                    val userObj = userIdElement.asJsonObject
                    User(
                        _id = userObj.get("_id").asString,
                        name = userObj.get("name").asString,
                        avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull)
                            userObj.get("avatar").asString else null,
                        created_at = parseDate(userObj.get("created_at"), dateFormat) ?: Date()
                    )
                } else {
                    User(
                        _id = userIdElement.asString,
                        name = "Unknown",
                        avatar = null,
                        created_at = Date()
                    )
                }

                val role = memberObj.get("role").asString
                val id = if (memberObj.has("_id")) memberObj.get("_id").asString else null

                membersList.add(WorkspaceMember(userId, role, id))
            }
            membersList
        } else {
            emptyList()
        }

        // Parse channels list
        val channelsElement = jsonObject.get("channels")
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

        return Workspace(
            _id = jsonObject.get("_id").asString,
            name = jsonObject.get("name").asString,
            description = if (jsonObject.has("description") && !jsonObject.get("description").isJsonNull)
                jsonObject.get("description").asString else null,
            created_by = createdBy,
            created_at = parseDate(jsonObject.get("created_at"), dateFormat) ?: Date(),
            members = members,
            channels = channels
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