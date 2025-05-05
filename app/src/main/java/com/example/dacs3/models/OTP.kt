package com.example.dacs3.models

data class ResendOtpRequest(val email: String)
data class ResendOtpResponse(val message: String, val action: String, val data: Map<String,String>)

data class VerifyOtpRequest(val email: String, val otp: String)
data class VerifyOtpResponse(
    val message: String? = null,
    val error: String? = null,
    val action: String,
    val data: Map<String,String>? = null
)
