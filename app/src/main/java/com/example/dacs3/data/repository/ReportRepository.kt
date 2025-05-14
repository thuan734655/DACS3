package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.ReportApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.DailyReport
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val api: ReportApi
) {
    suspend fun getDailyReports(
        workspaceId: String? = null,
        userId: String? = null,
        date: String? = null
    ): Response<ApiResponse<List<DailyReport>>> {
        return try {
            api.getDailyReports(workspaceId, userId, date)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting daily reports", e)
            throw e
        }
    }
    
    suspend fun getDailyReportById(reportId: String): Response<ApiResponse<DailyReport>> {
        return try {
            api.getDailyReportById(reportId)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting daily report by id", e)
            throw e
        }
    }
    
    suspend fun createDailyReport(report: DailyReport): Response<ApiResponse<DailyReport>> {
        return try {
            api.createDailyReport(report)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error creating daily report", e)
            throw e
        }
    }
    
    suspend fun updateDailyReport(reportId: String, report: DailyReport): Response<ApiResponse<DailyReport>> {
        return try {
            api.updateDailyReport(reportId, report)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error updating daily report", e)
            throw e
        }
    }
    
    suspend fun deleteDailyReport(reportId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteDailyReport(reportId)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error deleting daily report", e)
            throw e
        }
    }
    
    suspend fun getReportStatistics(
        workspaceId: String,
        startDate: String? = null,
        endDate: String? = null
    ): Response<ApiResponse<Any>> {
        return try {
            api.getReportStatistics(workspaceId, startDate, endDate)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting report statistics", e)
            throw e
        }
    }
} 