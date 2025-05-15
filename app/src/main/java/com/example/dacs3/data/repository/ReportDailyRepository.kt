package com.example.dacs3.data.repository

import com.example.dacs3.data.local.entity.ReportDailyEntity
import com.example.dacs3.data.model.ReportDailyListResponse
import com.example.dacs3.data.model.ReportDailyResponse
import com.example.dacs3.data.model.ReportInProgressTask
import com.example.dacs3.data.model.ReportIssue
import com.example.dacs3.data.model.ReportPlannedTask
import com.example.dacs3.data.model.ReportTask
import com.example.dacs3.data.model.ReportsByDateResponse
import com.example.dacs3.data.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ReportDailyRepository : BaseRepository<ReportDailyEntity, String> {
    /**
     * Get reports by workspace ID from local database
     */
    fun getReportsByWorkspaceId(workspaceId: String): Flow<List<ReportDailyEntity>>
    
    /**
     * Get reports by user ID from local database
     */
    fun getReportsByUserId(userId: String): Flow<List<ReportDailyEntity>>
    
    /**
     * Get reports between dates from local database
     */
    fun getReportsBetweenDates(startDate: Date, endDate: Date): Flow<List<ReportDailyEntity>>
    
    /**
     * Get reports by date from local database
     */
    fun getReportsByDate(date: Date): Flow<List<ReportDailyEntity>>
    
    /**
     * Get reports by workspace and date from local database
     */
    fun getReportsByWorkspaceAndDate(workspaceId: String, date: Date): Flow<List<ReportDailyEntity>>
    
    /**
     * Get reports by user and workspace from local database
     */
    fun getReportsByUserAndWorkspace(userId: String, workspaceId: String): Flow<List<ReportDailyEntity>>
    
    /**
     * Get all reports from remote API with pagination and filters
     */
    suspend fun getAllReportsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        userId: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ReportDailyListResponse
    
    /**
     * Get my reports from remote API
     */
    suspend fun getMyReportsFromApi(
        page: Int? = null,
        limit: Int? = null,
        workspaceId: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ReportDailyListResponse
    
    /**
     * Get reports by date from remote API
     */
    suspend fun getReportsByDateFromApi(
        date: String,
        workspaceId: String? = null
    ): ReportsByDateResponse
    
    /**
     * Get report by ID from remote API
     */
    suspend fun getReportByIdFromApi(id: String): ReportDailyResponse
    
    /**
     * Create or update a report on the remote API
     */
    suspend fun createOrUpdateReport(
        workspaceId: String,
        date: Date? = null,
        completedTasks: List<ReportTask>? = null,
        inProgressTasks: List<ReportInProgressTask>? = null,
        plannedTasks: List<ReportPlannedTask>? = null,
        issues: List<ReportIssue>? = null,
        generalNotes: String? = null
    ): ReportDailyResponse
    
    /**
     * Delete a report on the remote API
     */
    suspend fun deleteReportFromApi(id: String): Boolean
} 