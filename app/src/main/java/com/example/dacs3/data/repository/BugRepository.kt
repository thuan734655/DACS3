package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.BugEntity
import com.example.dacs3.data.model.BugListResponse
import com.example.dacs3.data.model.BugResponse
import com.example.dacs3.data.model.CommentResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface BugRepository : BaseRepository<BugEntity, String> {
    /**
     * Get bugs by workspace ID from local database
     */
    fun getBugsByWorkspaceId(workspaceId: String): Flow<List<BugEntity>>
    
    /**
     * Get bugs by task ID from local database
     */
    fun getBugsByTaskId(taskId: String): Flow<List<BugEntity>>
    
    /**
     * Get bugs by reported by from local database
     */
    fun getBugsByReportedBy(reportedBy: String): Flow<List<BugEntity>>
    
    /**
     * Get bugs by assigned to from local database
     */
    fun getBugsByAssignedTo(assignedTo: String): Flow<List<BugEntity>>
    
    /**
     * Get bugs by status from local database
     */
    fun getBugsByStatus(status: String): Flow<List<BugEntity>>
    
    /**
     * Get all bugs from remote API with pagination and filters
     */
    suspend fun getAllBugsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        taskId: String? = null,
        reportedBy: String? = null,
        assignedTo: String? = null,
        status: String? = null,
        severity: String? = null
    ): BugListResponse
    
    /**
     * Get bug by ID from remote API
     */
    suspend fun getBugByIdFromApi(id: String): BugResponse
    
    /**
     * Create a new bug on the remote API
     */
    suspend fun createBug(
        title: String,
        description: String?,
        workspaceId: String,
        taskId: String?,
        assignedTo: String?,
        status: String?,
        severity: String?,
        stepsToReproduce: String?,
        expectedBehavior: String?,
        actualBehavior: String?
    ): BugResponse
    
    /**
     * Update a bug on the remote API
     */
    suspend fun updateBug(
        id: String,
        title: String?,
        description: String?,
        taskId: String?,
        assignedTo: String?,
        status: String?,
        severity: String?,
        stepsToReproduce: String?,
        expectedBehavior: String?,
        actualBehavior: String?
    ): BugResponse
    
    /**
     * Delete a bug on the remote API
     */
    suspend fun deleteBugFromApi(id: String): Boolean
    
    /**
     * Add a comment to a bug
     */
    suspend fun addComment(bugId: String, content: String): CommentResponse
} 