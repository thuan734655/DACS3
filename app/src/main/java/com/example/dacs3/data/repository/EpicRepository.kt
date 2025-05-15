package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.EpicEntity
import com.example.dacs3.data.model.EpicListResponse
import com.example.dacs3.data.model.EpicResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface EpicRepository : BaseRepository<EpicEntity, String> {
    /**
     * Get epics by workspace ID from local database
     */
    fun getEpicsByWorkspaceId(workspaceId: String): Flow<List<EpicEntity>>
    
    /**
     * Get epics by status from local database
     */
    fun getEpicsByStatus(status: String): Flow<List<EpicEntity>>
    
    /**
     * Get epics by assigned to from local database
     */
    fun getEpicsByAssignedTo(assignedTo: String): Flow<List<EpicEntity>>
    
    /**
     * Get epics by sprint ID from local database
     */
    fun getEpicsBySprintId(sprintId: String): Flow<List<EpicEntity>>
    
    /**
     * Get all epics from remote API with pagination and filters
     */
    suspend fun getAllEpicsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        status: String? = null,
        assignedTo: String? = null,
        sprintId: String? = null
    ): EpicListResponse
    
    /**
     * Get epic by ID from remote API
     */
    suspend fun getEpicByIdFromApi(id: String): EpicResponse
    
    /**
     * Create a new epic on the remote API
     */
    suspend fun createEpic(
        title: String,
        description: String?,
        workspaceId: String,
        assignedTo: String?,
        status: String?,
        priority: String?,
        startDate: Date?,
        dueDate: Date?,
        sprintId: String?
    ): EpicResponse
    
    /**
     * Update an epic on the remote API
     */
    suspend fun updateEpic(
        id: String,
        title: String?,
        description: String?,
        assignedTo: String?,
        status: String?,
        priority: String?,
        startDate: Date?,
        dueDate: Date?,
        completedDate: Date?,
        sprintId: String?
    ): EpicResponse
    
    /**
     * Delete an epic on the remote API
     */
    suspend fun deleteEpicFromApi(id: String): Boolean
} 