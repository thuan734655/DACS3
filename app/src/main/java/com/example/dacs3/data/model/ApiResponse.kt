package com.example.dacs3.data.model

/**
 * Lớp wrapper chung cho phản hồi từ API
 */
data class ApiResponse<T>(
    val success: Boolean = false,
    val count: Int = 0,
    val data: T? = null,
    val message: String? = null
) 