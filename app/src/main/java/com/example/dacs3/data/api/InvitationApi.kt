package com.example.dacs3.data.api

import com.example.dacs3.data.model.InvitationListResponse
import com.example.dacs3.data.model.InvitationResponse
import com.example.dacs3.data.model.MessageResponse
import com.example.dacs3.data.model.SendInvitationRequest
import com.example.dacs3.data.model.WorkspaceResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InvitationApi {
    /**
     * Gửi lời mời tham gia workspace
     * Route: POST /invitations/workspace
     */
    @POST("invitations/workspace")
    suspend fun sendWorkspaceInvitation(
        @Body request: SendInvitationRequest
    ): InvitationResponse

    /**
     * Lấy danh sách lời mời
     * Route: GET /invitations
     * Với phân trang và lọc theo trạng thái
     */
    @GET("invitations")
    suspend fun getInvitations(
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): InvitationListResponse

    /**
     * Lấy chi tiết một lời mời
     * Route: GET /invitations/:invitationId
     */
    @GET("invitations/{invitationId}")
    suspend fun getInvitationById(
        @Path("invitationId") invitationId: String
    ): InvitationResponse

    /**
     * Chấp nhận lời mời
     * Route: POST /invitations/accept/:invitationId
     */
    @POST("invitations/accept/{invitationId}")
    suspend fun acceptInvitation(
        @Path("invitationId") invitationId: String
    ): WorkspaceResponse

    /**
     * Từ chối lời mời
     * Route: POST /invitations/reject/:invitationId
     */
    @POST("invitations/reject/{invitationId}")
    suspend fun rejectInvitation(
        @Path("invitationId") invitationId: String
    ): MessageResponse

    /**
     * Xóa lời mời
     * Route: DELETE /invitations/:invitationId
     */
    @DELETE("invitations/{invitationId}")
    suspend fun deleteInvitation(
        @Path("invitationId") invitationId: String
    ): MessageResponse
}
