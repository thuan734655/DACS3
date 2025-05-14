package com.example.dacs3.data.api

import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.DailyReport
import retrofit2.Response
import retrofit2.http.*

interface ReportApi {
    @GET("reports/daily")
    suspend fun getDailyReports(
        @Query("workspaceId") workspaceId: String? = null,
        @Query("userId") userId: String? = null,
        @Query("date") date: String? = null
    ): Response<ApiResponse<List<DailyReport>>>
    
    @GET("reports/daily/{reportId}")
    suspend fun getDailyReportById(@Path("reportId") reportId: String): Response<ApiResponse<DailyReport>>
    
    @POST("reports/daily")
    suspend fun createDailyReport(@Body report: DailyReport): Response<ApiResponse<DailyReport>>
    
    @PUT("reports/daily/{reportId}")
    suspend fun updateDailyReport(
        @Path("reportId") reportId: String,
        @Body report: DailyReport
    ): Response<ApiResponse<DailyReport>>
    
    @DELETE("reports/daily/{reportId}")
    suspend fun deleteDailyReport(@Path("reportId") reportId: String): Response<ApiResponse<Any>>
    
    @GET("reports/statistics")
    suspend fun getReportStatistics(
        @Query("workspaceId") workspaceId: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<Any>>
} 