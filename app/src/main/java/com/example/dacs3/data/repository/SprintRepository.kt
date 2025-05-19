package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.SprintEntity
import com.example.dacs3.data.model.SprintListResponse
import com.example.dacs3.data.model.SprintResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SprintRepository : BaseRepository<SprintEntity, String> {
    /**
     * Get sprints by workspace ID from local database
     */
    fun getSprintsByWorkspaceId(workspaceId: String): Flow<List<SprintEntity>>
    
    /**
     * Get sprints by status from local database
     */
    fun getSprintsByStatus(status: String): Flow<List<SprintEntity>>
    
    /**
     * Get sprints by user ID from local database
     */
    fun getSprintsByUserId(userId: String): Flow<List<SprintEntity>>
    
    /**
     * Get active sprints from local database
     */
    fun getActiveSprints(currentDate: Date): Flow<List<SprintEntity>>
    
    /**
     * Get all sprints from remote API with pagination and filters
     */
    suspend fun getAllSprintsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        status: String? = null
    ): SprintListResponse
    
    /**
     * Get sprints for the current user from remote API
     */
    suspend fun getSprintsForCurrentUserFromApi(): SprintListResponse
    
    /**
     * Get sprint by ID from remote API
     */
    suspend fun getSprintByIdFromApi(id: String): SprintResponse
    
    /**
     * Create a new sprint on the remote API
     */
    suspend fun createSprint(
        name: String,
        description: String?,
        workspaceId: String,
        startDate: Date,
        endDate: Date,
        goal: String?,
        status: String?
    ): SprintResponse
    
    /**
     * Update a sprint
     */
    suspend fun updateSprint(
        id: String,
        name: String? = null,
        description: String? = null,
        startDate: Date? = null,
        endDate: Date? = null,
        goal: String? = null,
        status: String? = null
    ): SprintResponse
    
    /**
     * Add items (tasks) to a sprint
     */
    suspend fun addItemsToSprint(sprintId: String, itemIds: List<String>): SprintResponse
    
    /**
     * Remove items (tasks) from a sprint
     */
    suspend fun removeItemsFromSprint(sprintId: String, itemIds: List<String>): SprintResponse
    
    /**
     * Get active sprints for the current date
     */
    suspend fun getActiveSprintsFromApi(workspaceId: String): SprintListResponse
    
    /**
     * Delete a sprint on the remote API
     */
    suspend fun deleteSprintFromApi(id: String): Boolean
    /**
     * Add items (tasks) to a sprint
     */
    suspend fun addItems(sprintId: String, tasks: List<String>?): SprintResponse
    
    /**
     * Remove items from a sprint
     */
    suspend fun removeItems(sprintId: String, tasks: List<String>?): SprintResponse
} 