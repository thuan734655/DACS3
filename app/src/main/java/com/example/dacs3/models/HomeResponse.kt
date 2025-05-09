package com.example.dacs3.models

data class HomeResponse(
    val message: String,
    val data: Map<String, Any>? = null
)
