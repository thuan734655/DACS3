package com.example.dacs3.data.repository

import com.example.dacs3.data.model.InvitationListResponse
import com.example.dacs3.data.model.InvitationResponse
import com.example.dacs3.data.model.MessageResponse
import com.example.dacs3.data.model.WorkspaceResponse

interface InvitationRepository {
    /**
     * Gửi lời mời tham gia workspace
     */
    suspend fun sendWorkspaceInvitation(email: String, workspaceId: String): InvitationResponse
    
    /**
     * Lấy danh sách lời mời với phân trang và lọc theo trạng thái
     */
    suspend fun getInvitations(status: String? = null, page: Int? = null, limit: Int? = null): InvitationListResponse
    
    /**
     * Lấy chi tiết một lời mời theo ID
     */
    suspend fun getInvitationById(invitationId: String): InvitationResponse
    
    /**
     * Chấp nhận lời mời tham gia workspace
     */
    suspend fun acceptInvitation(invitationId: String): WorkspaceResponse
    
    /**
     * Từ chối lời mời tham gia workspace
     */
    suspend fun rejectInvitation(invitationId: String): MessageResponse
    
    /**
     * Xóa lời mời
     */
    suspend fun deleteInvitation(invitationId: String): MessageResponse
}
