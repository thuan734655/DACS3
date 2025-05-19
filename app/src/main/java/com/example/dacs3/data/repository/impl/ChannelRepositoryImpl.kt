package com.example.dacs3.data.repository.impl

import com.example.dacs3.data.api.ChannelApi
import com.example.dacs3.data.local.dao.ChannelDao
import com.example.dacs3.data.local.entity.ChannelEntity
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.ChannelList
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.CreateChannelRequest
import com.example.dacs3.data.model.AddMemberRequest
import com.example.dacs3.data.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao,
    private val channelApi: ChannelApi
) : ChannelRepository {

    override suspend fun getAllChannelsFromApi(page: Int?, limit: Int?, workspaceId: String): ApiResponse<ChannelList> {
        return try {
            val response = channelApi.getAllChannels(page, limit, workspaceId)
            ApiResponse(
                success = response.success,
                data = ChannelList(response.count, response.total, response.data),
                message = "Lấy danh sách kênh thành công"
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                data = null,
                message = e.message ?: "Lỗi khi lấy danh sách kênh"
            )
        }
    }

    override suspend fun getChannelsByWorkspaceFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String
    ): ApiResponse<List<Channel>> {
        return try {
            val resp = channelApi.getAllChannels(page, limit, workspaceId)
            ApiResponse(
                success = resp.success,
                data    = resp.data,
                message = "Lấy danh sách kênh theo workspace thành công"
            )
        } catch (e: Exception) {
            ApiResponse(false, emptyList(), e.message ?: "Lỗi khi lấy kênh theo workspace")
        }
    }
    override suspend fun getChannelsByCreatedBy(userId: String): ApiResponse<List<Channel>> {
        return try {
            val response = channelApi.getAllChannels() // Thay thế apiService bằng channelApi
            // Xử lý response để lấy các channel được tạo bởi userId
            val filteredChannels = response.data.filter { it.created_by == userId }
            
            ApiResponse(
                success = true,
                data = filteredChannels,
                message = "Lấy danh sách kênh theo người tạo thành công"
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                data = emptyList(),
                message = e.message ?: "Lỗi khi lấy danh sách kênh theo người tạo"
            )
        }
    }

    override suspend fun createChannel(
        name: String,
        description: String?,
        workspaceId: String,
        createdBy: String,
        isPrivate: Boolean
    ): ApiResponse<Channel> {
        val request = CreateChannelRequest(
            name       = name,
            description= description,
            workspace_id = workspaceId,
            created_by = createdBy,
            is_private = isPrivate
        )
        return try {
            val resp = channelApi.createChannel(request)
            ApiResponse(resp.success, resp.data, "Tạo kênh thành công")
        } catch (e: Exception) {
            ApiResponse(false, null, e.message ?: "Lỗi khi tạo kênh")
        }
    }

    override suspend fun addMember(channelId: String, userId: String): ApiResponse<Channel> {
        return try {
            val request = AddMemberRequest(userId)
            val response = channelApi.addMember(channelId, request)
            ApiResponse(
                success = response.success,
                data = response.data,
                message = "Thêm thành viên vào kênh thành công"
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                data = null,
                message = e.message ?: "Lỗi khi thêm thành viên vào kênh"
            )
        }
    }

    override suspend fun joinChannel(channelId: String): ApiResponse<Channel> {
        return try {
            val response = channelApi.joinChannel(channelId)
            ApiResponse(
                success = response.success,
                data = response.data,
                message = "Tham gia kênh thành công"
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                data = null,
                message = e.message ?: "Lỗi khi tham gia kênh"
            )
        }
    }

    override suspend fun leaveChannel(channelId: String): ApiResponse<Channel> {
        return try {
            val response = channelApi.leaveChannel(channelId)
            ApiResponse(
                success = true,
                data = null,
                message = "Rời khỏi kênh thành công"
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                data = null,
                message = e.message ?: "Lỗi khi rời khỏi kênh"
            )
        }
    }

    override suspend fun getChannelsByWorkspaceId(workspaceId: String): Flow<List<ChannelEntity>> {
        // Triển khai logic để lấy danh sách ChannelEntity theo workspaceId
        // Ví dụ: return channelDao.getChannelsByWorkspaceId(workspaceId)
        TODO("Implement this method")
    }

}