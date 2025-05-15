package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.ChannelApi
import com.example.dacs3.data.local.dao.ChannelDao
import com.example.dacs3.data.local.entity.ChannelEntity
import com.example.dacs3.data.model.AddMemberRequest
import com.example.dacs3.data.model.ChannelListResponse
import com.example.dacs3.data.model.ChannelResponse
import com.example.dacs3.data.model.CreateChannelRequest
import com.example.dacs3.data.model.UpdateChannelRequest
import com.example.dacs3.data.repository.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao,
    private val channelApi: ChannelApi
) : ChannelRepository {
    
    private val TAG = "ChannelRepositoryImpl"
    
    override fun getAll(): Flow<List<ChannelEntity>> {
        return channelDao.getAllChannels()
    }
    
    override suspend fun getById(id: String): ChannelEntity? {
        return channelDao.getChannelById(id)
    }
    
    override suspend fun insert(item: ChannelEntity) {
        channelDao.insertChannel(item)
    }
    
    override suspend fun insertAll(items: List<ChannelEntity>) {
        channelDao.insertChannels(items)
    }
    
    override suspend fun update(item: ChannelEntity) {
        channelDao.updateChannel(item)
    }
    
    override suspend fun delete(item: ChannelEntity) {
        channelDao.deleteChannel(item)
    }
    
    override suspend fun deleteById(id: String) {
        channelDao.deleteChannelById(id)
    }
    
    override suspend fun deleteAll() {
        channelDao.deleteAllChannels()
    }
    
    override suspend fun sync() {
        try {
            val response = channelApi.getAllChannels()
            if (response.success && response.data != null) {
                val channels = response.data.map { ChannelEntity.fromChannel(it) }
                channelDao.insertChannels(channels)
                Log.d(TAG, "Successfully synced ${channels.size} channels")
            } else {
                Log.w(TAG, "Failed to sync channels")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing channels", e)
        }
    }
    
    override fun getChannelsByWorkspaceId(workspaceId: String): Flow<List<ChannelEntity>> {
        return channelDao.getChannelsByWorkspaceId(workspaceId)
    }
    
    override fun getChannelsByCreatedBy(createdBy: String): Flow<List<ChannelEntity>> {
        return channelDao.getChannelsByCreatedBy(createdBy)
    }
    
    override suspend fun getAllChannelsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?
    ): ChannelListResponse {
        return try {
            val response = channelApi.getAllChannels(page, limit, workspaceId)
            
            // If successful, store channels in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntities = response.data.map { ChannelEntity.fromChannel(it) }
                    channelDao.insertChannels(channelEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching channels from API", e)
            // Return empty response with success=false when API fails
            ChannelListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getChannelByIdFromApi(id: String): ChannelResponse {
        return try {
            val response = channelApi.getChannelById(id)
            
            // If successful, store channel in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntity = ChannelEntity.fromChannel(response.data)
                    channelDao.insertChannel(channelEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching channel from API", e)
            // Return empty response with success=false when API fails
            ChannelResponse(false, null)
        }
    }
    
    override suspend fun createChannel(
        name: String,
        description: String?,
        workspaceId: String,
        createdBy: String,
        isPrivate: Boolean
    ): ChannelResponse {
        return try {
            val request = CreateChannelRequest(name, description, workspaceId, createdBy, isPrivate)
            val response = channelApi.createChannel(request)
            
            // If successful, store channel in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntity = ChannelEntity.fromChannel(response.data)
                    channelDao.insertChannel(channelEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating channel", e)
            // Return empty response with success=false when API fails
            ChannelResponse(false, null)
        }
    }
    
    override suspend fun updateChannel(
        id: String,
        name: String?,
        description: String?,
        isPrivate: Boolean?
    ): ChannelResponse {
        return try {
            val request = UpdateChannelRequest(name, description, isPrivate)
            val response = channelApi.updateChannel(id, request)
            
            // If successful, update channel in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntity = ChannelEntity.fromChannel(response.data)
                    channelDao.updateChannel(channelEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating channel", e)
            // Return empty response with success=false when API fails
            ChannelResponse(false, null)
        }
    }
    
    override suspend fun deleteChannelFromApi(id: String): Boolean {
        return try {
            val response = channelApi.deleteChannel(id)
            
            // If successful, delete channel from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    channelDao.deleteChannelById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting channel", e)
            false
        }
    }
    
    override suspend fun addMember(channelId: String, userId: String, role: String?): ChannelResponse {
        return try {
            val request = AddMemberRequest(userId, role)
            val response = channelApi.addMember(channelId, request)
            
            // If successful, update channel in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntity = ChannelEntity.fromChannel(response.data)
                    channelDao.updateChannel(channelEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding member to channel", e)
            // Return empty response with success=false when API fails
            ChannelResponse(false, null)
        }
    }
    
    override suspend fun removeMember(channelId: String, userId: String): ChannelResponse {
        return try {
            val response = channelApi.removeMember(channelId, userId)
            
            // If successful, update channel in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntity = ChannelEntity.fromChannel(response.data)
                    channelDao.updateChannel(channelEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error removing member from channel", e)
            // Return empty response with success=false when API fails
            ChannelResponse(false, null)
        }
    }
    
    override suspend fun joinChannel(channelId: String): ChannelResponse {
        return try {
            val response = channelApi.joinChannel(channelId)
            
            // If successful, update channel in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val channelEntity = ChannelEntity.fromChannel(response.data)
                    channelDao.updateChannel(channelEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error joining channel", e)
            // Return empty response with success=false when API fails
            ChannelResponse(false, null)
        }
    }
    
    override suspend fun leaveChannel(channelId: String): Boolean {
        return try {
            val response = channelApi.leaveChannel(channelId)
            
            // If successful, update channel in local database
            // Note: We're not deleting the channel locally because the user might still need to see it,
            // even if they're no longer a member
            if (response.success) {
                withContext(Dispatchers.IO) {
                    val channel = channelDao.getChannelById(channelId)
                    if (channel != null) {
                        // Update the channel through the API to get the latest member list
                        val updatedChannel = getChannelByIdFromApi(channelId)
                        if (updatedChannel.success && updatedChannel.data != null) {
                            val channelEntity = ChannelEntity.fromChannel(updatedChannel.data)
                            channelDao.updateChannel(channelEntity)
                        }
                    }
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error leaving channel", e)
            false
        }
    }
} 