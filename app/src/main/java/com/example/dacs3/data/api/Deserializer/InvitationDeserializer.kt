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
        val id = jsonObject.get("_id").asString

        // Xử lý type_invitation
        val typeInvitation = jsonObject.get("type_invitation").asString

        // Xử lý workspace_id - có thể là string hoặc object
        val workspaceId = deserializeWorkspace(jsonObject.get("workspace_id"), context)

        // Xử lý user_id - có thể là string hoặc object
        val userId = deserializeUser(jsonObject.get("user_id"), context)

        // Xử lý email
        val email = jsonObject.get("email").asString

        // Xử lý invited_by - có thể là string hoặc object
        val invitedBy = deserializeUser(jsonObject.get("invited_by"), context)

        // Xử lý status
        val status = jsonObject.get("status").asString

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

        return if (element.isJsonPrimitive) {
            element.asString // Trả về ID dạng string
        } else {
            // Đây là một đối tượng Workspace đầy đủ
            context.deserialize<Workspace>(element, Workspace::class.java)
        }
    }

    /**
     * Chuyển đổi JsonElement thành User hoặc giữ nguyên String ID
     */
    private fun deserializeUser(element: JsonElement?, context: JsonDeserializationContext): Any {
        if (element == null || element.isJsonNull) {
            return ""
        }

        return if (element.isJsonPrimitive) {
            element.asString // Trả về ID dạng string
        } else {
            // Đây là một đối tượng User đầy đủ
            context.deserialize<User>(element, User::class.java)
        }
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
