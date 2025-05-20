package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.InvitationApi
import com.example.dacs3.data.model.InvitationListResponse
import com.example.dacs3.data.model.InvitationResponse
import com.example.dacs3.data.model.MessageResponse
import com.example.dacs3.data.model.SendInvitationRequest
import com.example.dacs3.data.model.WorkspaceResponse
import com.example.dacs3.data.repository.InvitationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvitationRepositoryImpl @Inject constructor(
    private val invitationApi: InvitationApi
) : InvitationRepository {
    
    private val TAG = "InvitationRepositoryImpl"
    
    override suspend fun sendWorkspaceInvitation(email: String, workspaceId: String): InvitationResponse {
        return try {
            val request = SendInvitationRequest(email, workspaceId)
            val response = invitationApi.sendWorkspaceInvitation(request)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error sending workspace invitation", e)
            // Return empty response with success=false when API fails
            InvitationResponse(false, null, "Failed to send invitation: ${e.message}")
        }
    }
    
    override suspend fun getInvitations(status: String?, page: Int?, limit: Int?): InvitationListResponse {
        return try {
            val response = invitationApi.getInvitations(status, page, limit)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching invitations", e)
            // Return empty response with success=false when API fails
            InvitationListResponse(false, emptyList())
        }
    }
    
    override suspend fun getInvitationById(invitationId: String): InvitationResponse {
        return try {
            val response = invitationApi.getInvitationById(invitationId)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching invitation details", e)
            // Return empty response with success=false when API fails
            InvitationResponse(false, null, "Failed to fetch invitation: ${e.message}")
        }
    }
    
    override suspend fun acceptInvitation(invitationId: String): WorkspaceResponse {
        return try {
            val response = invitationApi.acceptInvitation(invitationId)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error accepting invitation", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun rejectInvitation(invitationId: String): MessageResponse {
        return try {
            val response = invitationApi.rejectInvitation(invitationId)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error rejecting invitation", e)
            // Return empty response with success=false when API fails
            MessageResponse(false, "Failed to reject invitation: ${e.message}")
        }
    }
    
    override suspend fun deleteInvitation(invitationId: String): MessageResponse {
        return try {
            val response = invitationApi.deleteInvitation(invitationId)
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting invitation", e)
            // Return empty response with success=false when API fails
            MessageResponse(false, "Failed to delete invitation: ${e.message}")
        }
    }
}
