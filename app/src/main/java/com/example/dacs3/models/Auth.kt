package com.example.dacs3.models

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
    val type: String,
    val deviceID: String? = null
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
