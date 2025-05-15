package com.example.dacs3.data.api

import com.example.dacs3.data.model.*
import retrofit2.http.*

interface ReportDailyApi {
    // GET all reports with pagination
    @GET("reports")
    suspend fun getAllReports(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("user_id") userId: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ReportDailyListResponse

    // GET my reports
    @GET("reports/me")
    suspend fun getMyReports(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("workspace_id") workspaceId: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ReportDailyListResponse

    // GET reports by date
    @GET("reports/by-date")
    suspend fun getReportsByDate(
        @Query("date") date: String,
        @Query("workspace_id") workspaceId: String? = null
    ): ReportsByDateResponse

    // GET report by ID
    @GET("reports/{id}")
    suspend fun getReportById(@Path("id") id: String): ReportDailyResponse

    // POST create or update report
    @POST("reports")
    suspend fun createOrUpdateReport(@Body request: CreateOrUpdateReportRequest): ReportDailyResponse

    // DELETE report
    @DELETE("reports/{id}")
    suspend fun deleteReport(@Path("id") id: String): MessageResponse
} 