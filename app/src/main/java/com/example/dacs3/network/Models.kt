package com.example.dacs3.network

data class RegisterRequest(
    val username: String,
    val email: String,
    val contactNumber: String,
    val password: String
)
data class RegisterResponse(val message: String)

data class LoginRequest(
    val accountName: String,
    val password: String,
    val type: String
)
data class LoginResponse(
    val message: String,
    val token: String,
    val account: AccountInfo
)
data class AccountInfo(
    val username: String,
    val email: String,
    val contactNumber: String
)

data class ResendOtpRequest(val email: String)
data class ResendOtpResponse(val message: String, val action: String, val data: Map<String,String>)

data class VerifyOtpRequest(val email: String, val otp: String)
data class VerifyOtpResponse(
    val message: String? = null,
    val error: String? = null,
    val action: String,
    val data: Map<String,String>? = null
)
