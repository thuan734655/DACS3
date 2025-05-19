package com.example.dacs3.data.local.dao

import androidx.room.*
import com.example.dacs3.data.local.entity.ReportDailyEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ReportDailyDao {
//    @Query("SELECT * FROM reports_daily")
//    fun getAllReports(): Flow<List<ReportDailyEntity>>
//
//    @Query("SELECT * FROM reports_daily WHERE _id = :id")
//    suspend fun getReportById(id: String): ReportDailyEntity?
//
//    @Query("SELECT * FROM reports_daily WHERE workspace_id = :workspaceId")
//    fun getReportsByWorkspaceId(workspaceId: String): Flow<List<ReportDailyEntity>>
//
//    @Query("SELECT * FROM reports_daily WHERE user_id = :userId")
//    fun getReportsByUserId(userId: String): Flow<List<ReportDailyEntity>>
//
//    @Query("SELECT * FROM reports_daily WHERE date BETWEEN :startDate AND :endDate")
//    fun getReportsBetweenDates(startDate: Date, endDate: Date): Flow<List<ReportDailyEntity>>
//
//    @Query("SELECT * FROM reports_daily WHERE date = :date")
//    fun getReportsByDate(date: Date): Flow<List<ReportDailyEntity>>
//
//    @Query("SELECT * FROM reports_daily WHERE workspace_id = :workspaceId AND date = :date")
//    fun getReportsByWorkspaceAndDate(workspaceId: String, date: Date): Flow<List<ReportDailyEntity>>
//
//    @Query("SELECT * FROM reports_daily WHERE user_id = :userId AND workspace_id = :workspaceId")
//    fun getReportsByUserAndWorkspace(userId: String, workspaceId: String): Flow<List<ReportDailyEntity>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertReport(report: ReportDailyEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertReports(reports: List<ReportDailyEntity>)
//
//    @Update
//    suspend fun updateReport(report: ReportDailyEntity)
//
//    @Delete
//    suspend fun deleteReport(report: ReportDailyEntity)
//
//    @Query("DELETE FROM reports_daily WHERE _id = :id")
//    suspend fun deleteReportById(id: String)
//
//    @Query("DELETE FROM reports_daily")
//    suspend fun deleteAllReports()
} 