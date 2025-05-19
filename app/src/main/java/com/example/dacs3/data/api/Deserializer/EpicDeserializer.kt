package com.example.dacs3.data.api.deserializer

import com.example.dacs3.data.model.*
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

/**
 * Custom deserializer for Epic responses that handles inconsistencies in the API response format
 */
class EpicDeserializer : JsonDeserializer<Epic> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Epic {
        val jsonObject = json.asJsonObject
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        
        // Parse workspace from workspace_id field
        val workspaceObj = jsonObject.get("workspace_id").asJsonObject
        val workspace = parseWorkspace(workspaceObj, dateFormat)
        
        // Parse created_by field as User
        val createdByElement = jsonObject.get("created_by")
        val createdBy = if (createdByElement.isJsonObject) {
            context.deserialize<User>(createdByElement, User::class.java)
        } else {
            // Fallback for string ID
            User(
                _id = createdByElement.asString,
                name = "Unknown",
                avatar = null,
                created_at = Date()
            )
        }
        
        // Parse dates
        val startDate = parseDate(jsonObject.get("start_date"), dateFormat)
        val dueDate = parseDate(jsonObject.get("due_date"), dateFormat)
        val completedDate = parseDate(jsonObject.get("completed_date"), dateFormat)
        val createdAt = parseDate(jsonObject.get("created_at"), dateFormat) ?: Date()
        val updatedAt = parseDate(jsonObject.get("updated_at"), dateFormat) ?: Date()
        
        // Parse tasks list
        val tasksElement = jsonObject.get("tasks")
        val tasks = if (tasksElement != null && !tasksElement.isJsonNull) {
            val taskList = mutableListOf<String>()
            val tasksArray = tasksElement.asJsonArray
            for (taskElement in tasksArray) {
                taskList.add(taskElement.asString)
            }
            taskList
        } else {
            null
        }

        return Epic(
            _id = jsonObject.get("_id").asString,
            title = jsonObject.get("title").asString,
            description = if (jsonObject.has("description") && !jsonObject.get("description").isJsonNull) 
                jsonObject.get("description").asString else null,
            workspace_id = workspace,
            created_by = createdBy,
            assigned_to = if (jsonObject.has("assigned_to") && !jsonObject.get("assigned_to").isJsonNull) 
                jsonObject.get("assigned_to").asString else null,
            status = jsonObject.get("status").asString,
            priority = jsonObject.get("priority").asString,
            start_date = startDate,
            due_date = dueDate,
            completed_date = completedDate,
            sprint_id = if (jsonObject.has("sprint_id") && !jsonObject.get("sprint_id").isJsonNull) 
                jsonObject.get("sprint_id").asString else null,
            tasks = tasks,
            created_at = createdAt,
            updated_at = updatedAt
        )
    }
    
    private fun parseWorkspace(workspaceObj: JsonObject, dateFormat: SimpleDateFormat): Workspace {
        // For workspace.created_by field which might be a string ID instead of a User object
        val createdByElement = workspaceObj.get("created_by")
        val createdBy: User = if (createdByElement.isJsonPrimitive) {
            // If created_by is a string ID
            User(
                _id = createdByElement.asString,
                name = "Unknown",
                avatar = null,
                created_at = parseDate(workspaceObj.get("created_at"), dateFormat) ?: Date()
            )
        } else {
            // If created_by is a User object
            User(
                _id = createdByElement.asJsonObject.get("_id").asString,
                name = createdByElement.asJsonObject.get("name").asString,
                avatar = if (createdByElement.asJsonObject.has("avatar") && 
                        !createdByElement.asJsonObject.get("avatar").isJsonNull)
                    createdByElement.asJsonObject.get("avatar").asString else null,
                created_at = parseDate(createdByElement.asJsonObject.get("created_at"), dateFormat) ?: Date()
            )
        }
        
        // Parse members list with special handling for user_id
        val membersElement = workspaceObj.get("members")
        val members = if (membersElement != null && !membersElement.isJsonNull) {
            val membersList = mutableListOf<WorkspaceMember>()
            val membersArray = membersElement.asJsonArray
            
            for (memberElement in membersArray) {
                val memberObj = memberElement.asJsonObject
                val userIdElement = memberObj.get("user_id")
                
                // Handle user_id that can be either a string or a User object
                val userId: User = if (userIdElement.isJsonObject) {
                    val userObj = userIdElement.asJsonObject
                    User(
                        _id = userObj.get("_id").asString,
                        name = userObj.get("name").asString,
                        avatar = if (userObj.has("avatar") && !userObj.get("avatar").isJsonNull) 
                            userObj.get("avatar").asString else null,
                        created_at = parseDate(userObj.get("created_at"), dateFormat) ?: Date()
                    )
                } else {
                    // Fallback for legacy data that might have user_id as a string
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
            null
        }
        
        // Parse channels list
        val channelsElement = workspaceObj.get("channels")
        val channels = if (channelsElement != null && !channelsElement.isJsonNull) {
            val channelsList = mutableListOf<String>()
            val channelsArray = channelsElement.asJsonArray
            for (channelElement in channelsArray) {
                channelsList.add(channelElement.asString)
            }
            channelsList
        } else {
            null
        }
        
        return Workspace(
            _id = workspaceObj.get("_id").asString,
            name = workspaceObj.get("name").asString,
            description = if (workspaceObj.has("description") && !workspaceObj.get("description").isJsonNull) 
                workspaceObj.get("description").asString else null,
            created_by = createdBy,
            created_at = parseDate(workspaceObj.get("created_at"), dateFormat) ?: Date(),
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
