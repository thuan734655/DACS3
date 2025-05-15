package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.WorkspaceEntity
import com.example.dacs3.data.model.WorkspaceListResponse
import com.example.dacs3.data.model.WorkspaceResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository : BaseRepository<WorkspaceEntity, String> {
    /**
     * Get workspaces by user ID from local database
     */
    fun getWorkspacesByUserId(userId: String): Flow<List<WorkspaceEntity>>
    
    /**
     * Get all workspaces from remote API with pagination
     */
    suspend fun getAllWorkspacesFromApi(page: Int? = null, limit: Int? = null): WorkspaceListResponse
    
    /**
     * Get all workspaces by user ID from remote API
     */
    suspend fun getAllWorkspacesByUserIdFromApi(userId: String, page: Int? = null, limit: Int? = null): WorkspaceListResponse
    
    /**
     * Get workspace by ID from remote API
     */
    suspend fun getWorkspaceByIdFromApi(id: String): WorkspaceResponse
    
    /**
     * Create a new workspace on the remote API
     */
    suspend fun createWorkspace(name: String, description: String?): WorkspaceResponse
    
    /**
     * Update a workspace on the remote API
     */
    suspend fun updateWorkspace(id: String, name: String?, description: String?): WorkspaceResponse
    
    /**
     * Delete a workspace on the remote API
     */
    suspend fun deleteWorkspaceFromApi(id: String): Boolean
    
    /**
     * Add a member to a workspace
     */
    suspend fun addMember(workspaceId: String, userId: String, role: String?): WorkspaceResponse
    
    /**
     * Remove a member from a workspace
     */
    suspend fun removeMember(workspaceId: String, userId: String): WorkspaceResponse
    
    /**
     * Join a workspace
     */
    suspend fun joinWorkspace(workspaceId: String): WorkspaceResponse
    
    /**
     * Leave a workspace
     */
    suspend fun leaveWorkspace(workspaceId: String): Boolean
} 