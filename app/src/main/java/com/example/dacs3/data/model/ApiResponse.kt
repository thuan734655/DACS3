package com.example.dacs3.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String
)