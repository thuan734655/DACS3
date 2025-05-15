package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.ReportDailyApi
import com.example.dacs3.data.local.dao.ReportDailyDao
import com.example.dacs3.data.local.entity.ReportDailyEntity
import com.example.dacs3.data.model.CreateOrUpdateReportRequest
import com.example.dacs3.data.model.ReportDailyListResponse
import com.example.dacs3.data.model.ReportDailyResponse
import com.example.dacs3.data.model.ReportInProgressTask
import com.example.dacs3.data.model.ReportIssue
import com.example.dacs3.data.model.ReportPlannedTask
import com.example.dacs3.data.model.ReportTask
import com.example.dacs3.data.model.ReportsByDateResponse
import com.example.dacs3.data.repository.ReportDailyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportDailyRepositoryImpl @Inject constructor(
    private val reportDailyDao: ReportDailyDao,
    private val reportDailyApi: ReportDailyApi
) : ReportDailyRepository {
    
    private val TAG = "ReportDailyRepositoryImpl"
    
    override fun getAll(): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getAllReports()
    }
    
    override suspend fun getById(id: String): ReportDailyEntity? {
        return reportDailyDao.getReportById(id)
    }
    
    override suspend fun insert(item: ReportDailyEntity) {
        reportDailyDao.insertReport(item)
    }
    
    override suspend fun insertAll(items: List<ReportDailyEntity>) {
        reportDailyDao.insertReports(items)
    }
    
    override suspend fun update(item: ReportDailyEntity) {
        reportDailyDao.updateReport(item)
    }
    
    override suspend fun delete(item: ReportDailyEntity) {
        reportDailyDao.deleteReport(item)
    }
    
    override suspend fun deleteById(id: String) {
        reportDailyDao.deleteReportById(id)
    }
    
    override suspend fun deleteAll() {
        reportDailyDao.deleteAllReports()
    }
    
    override suspend fun sync() {
        try {
            val response = reportDailyApi.getAllReports()
            if (response.success && response.data != null) {
                val reports = response.data.map { ReportDailyEntity.fromReportDaily(it) }
                reportDailyDao.insertReports(reports)
                Log.d(TAG, "Successfully synced ${reports.size} reports")
            } else {
                Log.w(TAG, "Failed to sync reports")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing reports", e)
        }
    }
    
    override fun getReportsByWorkspaceId(workspaceId: String): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getReportsByWorkspaceId(workspaceId)
    }
    
    override fun getReportsByUserId(userId: String): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getReportsByUserId(userId)
    }
    
    override fun getReportsBetweenDates(startDate: Date, endDate: Date): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getReportsBetweenDates(startDate, endDate)
    }
    
    override fun getReportsByDate(date: Date): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getReportsByDate(date)
    }
    
    override fun getReportsByWorkspaceAndDate(workspaceId: String, date: Date): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getReportsByWorkspaceAndDate(workspaceId, date)
    }
    
    override fun getReportsByUserAndWorkspace(userId: String, workspaceId: String): Flow<List<ReportDailyEntity>> {
        return reportDailyDao.getReportsByUserAndWorkspace(userId, workspaceId)
    }
    
    override suspend fun getAllReportsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        userId: String?,
        startDate: String?,
        endDate: String?
    ): ReportDailyListResponse {
        return try {
            val response = reportDailyApi.getAllReports(
                page, limit, workspaceId, userId, startDate, endDate
            )
            
            // If successful, store reports in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val reportEntities = response.data.map { ReportDailyEntity.fromReportDaily(it) }
                    reportDailyDao.insertReports(reportEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching reports from API", e)
            // Return empty response with success=false when API fails
            ReportDailyListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getMyReportsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        startDate: String?,
        endDate: String?
    ): ReportDailyListResponse {
        return try {
            val response = reportDailyApi.getMyReports(
                page, limit, workspaceId, startDate, endDate
            )
            
            // If successful, store reports in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val reportEntities = response.data.map { ReportDailyEntity.fromReportDaily(it) }
                    reportDailyDao.insertReports(reportEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching my reports from API", e)
            // Return empty response with success=false when API fails
            ReportDailyListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getReportsByDateFromApi(
        date: String,
        workspaceId: String?
    ): ReportsByDateResponse {
        return try {
            val response = reportDailyApi.getReportsByDate(date, workspaceId)
            
            // If successful, store reports in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val reportEntities = response.data.map { ReportDailyEntity.fromReportDaily(it) }
                    reportDailyDao.insertReports(reportEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching reports by date from API", e)
            // Return empty response with success=false when API fails
            ReportsByDateResponse(false, 0, emptyList(), date)
        }
    }
    
    override suspend fun getReportByIdFromApi(id: String): ReportDailyResponse {
        return try {
            val response = reportDailyApi.getReportById(id)
            
            // If successful, store report in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val reportEntity = ReportDailyEntity.fromReportDaily(response.data)
                    reportDailyDao.insertReport(reportEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching report from API", e)
            // Return empty response with success=false when API fails
            ReportDailyResponse(false, null)
        }
    }
    
    override suspend fun createOrUpdateReport(
        workspaceId: String,
        date: Date?,
        completedTasks: List<ReportTask>?,
        inProgressTasks: List<ReportInProgressTask>?,
        plannedTasks: List<ReportPlannedTask>?,
        issues: List<ReportIssue>?,
        generalNotes: String?
    ): ReportDailyResponse {
        return try {
            val request = CreateOrUpdateReportRequest(
                workspaceId, date, completedTasks, inProgressTasks, 
                plannedTasks, issues, generalNotes
            )
            val response = reportDailyApi.createOrUpdateReport(request)
            
            // If successful, store report in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val reportEntity = ReportDailyEntity.fromReportDaily(response.data)
                    reportDailyDao.insertReport(reportEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating or updating report", e)
            // Return empty response with success=false when API fails
            ReportDailyResponse(false, null)
        }
    }
    
    override suspend fun deleteReportFromApi(id: String): Boolean {
        return try {
            val response = reportDailyApi.deleteReport(id)
            
            // If successful, delete report from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    reportDailyDao.deleteReportById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting report", e)
            false
        }
    }
} 