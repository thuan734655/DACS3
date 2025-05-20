package com.example.dacs3.data.api.deserializer

import com.example.dacs3.data.model.Invitation
import com.example.dacs3.data.model.User
import com.example.dacs3.data.model.Workspace
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Custom deserializer cho Invitation để xử lý các trường hợp khi workspaceId, userId, 
 * hoặc invitedBy có thể là String ID hoặc đối tượng đầy đủ
 */
class InvitationDeserializer : JsonDeserializer<Invitation> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Invitation {
        val jsonObject = json.asJsonObject

        // Xử lý ID
        val id = jsonObject.get("_id")?.asString ?: ""

        // Xử lý type_invitation
        val typeInvitation = jsonObject.get("type_invitation")?.asString ?: "workspace"

        // Xử lý workspace_id - có thể là string hoặc object
        val workspaceId = deserializeWorkspace(jsonObject.get("workspace_id"), context)

        // Xử lý user_id - có thể là string hoặc object
        val userId = deserializeUser(jsonObject.get("user_id"), context)

        // Xử lý email
        val email = jsonObject.get("email")?.asString ?: ""

        // Xử lý invited_by - có thể là string hoặc object
        val invitedBy = deserializeUser(jsonObject.get("invited_by"), context)

        // Xử lý status
        val status = jsonObject.get("status")?.asString ?: "pending"

        // Xử lý created_at và updated_at
        val createdAt = deserializeDate(jsonObject.get("created_at"))
        val updatedAt = deserializeDate(jsonObject.get("updated_at"))

        return Invitation(
            id = id,
            typeInvitation = typeInvitation,
            workspaceId = workspaceId,
            userId = userId,
            email = email,
            invitedBy = invitedBy,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    /**
     * Chuyển đổi JsonElement thành Workspace hoặc giữ nguyên String ID
     */
    private fun deserializeWorkspace(element: JsonElement?, context: JsonDeserializationContext): Any {
        if (element == null || element.isJsonNull) {
            return ""
        }

        return try {
            if (element.isJsonPrimitive) {
                element.asString // Trả về ID dạng string
            } else {
                // Đây là một đối tượng Workspace đầy đủ
                try {
                    context.deserialize<Workspace>(element, Workspace::class.java)
                } catch (e: Exception) {
                    // Fallback if workspace deserializer fails
                    val jsonObject = element.asJsonObject
                    val id = jsonObject.get("_id")?.asString ?: ""
                    val name = jsonObject.get("name")?.asString ?: "Unknown Workspace"
                    Workspace(
                        _id = id,
                        name = name,
                        description = null,
                        created_by = User("", "Unknown", null, Date()),
                        created_at = Date(),
                        members = emptyList(),
                        channels = emptyList()
                    )
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Chuyển đổi JsonElement thành User hoặc giữ nguyên String ID
     */
    private fun deserializeUser(element: JsonElement?, context: JsonDeserializationContext): Any {
        if (element == null || element.isJsonNull) {
            return ""
        }

        return try {
            if (element.isJsonPrimitive) {
                // For invitation API, we'll create a User object with the ID
                // to maintain consistency in the app
                val id = element.asString
                User(
                    _id = id,
                    name = getUsernameById(id),  // Try to get username if known
                    avatar = null,
                    created_at = Date()
                )
            } else {
                try {
                    // Đây là một đối tượng User đầy đủ
                    val jsonObject = element.asJsonObject
                    val id = jsonObject.get("_id")?.asString ?: ""
                    val username = if (jsonObject.has("username") && !jsonObject.get("username").isJsonNull)
                        jsonObject.get("username").asString
                    else if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull)
                        jsonObject.get("name").asString
                    else
                        getUsernameById(id)
                        
                    User(
                        _id = id,
                        name = username,
                        avatar = if (jsonObject.has("avatar") && !jsonObject.get("avatar").isJsonNull)
                            jsonObject.get("avatar").asString else null,
                        created_at = deserializeDate(jsonObject.get("created_at")) ?: Date()
                    )
                } catch (e: Exception) {
                    // Fallback if user deserializer fails
                    val jsonObject = element.asJsonObject
                    val id = jsonObject.get("_id")?.asString ?: ""
                    User(
                        _id = id,
                        name = getUsernameById(id),
                        avatar = null,
                        created_at = Date()
                    )
                }
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Try to determine a username from an ID
     * In a real app, this might call a repository to get user details
     */
    private fun getUsernameById(id: String): String {
        // For now, just return a nicer label instead of "Unknown"
        return "User $id"
    }

    /**
     * Chuyển đổi JsonElement thành Date
     */
    private fun deserializeDate(element: JsonElement?): Date {
        if (element == null || element.isJsonNull) {
            return Date()
        }

        return try {
            if (element.isJsonPrimitive) {
                dateFormat.parse(element.asString) ?: Date()
            } else {
                Date()
            }
        } catch (e: Exception) {
            Date()
        }
    }
}
