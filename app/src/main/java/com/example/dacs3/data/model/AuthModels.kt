package com.example.dacs3.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val contactNumber: String,
    val password: String
)

data class LoginRequest(
    val accountName: String,
    val password: String,
    val type: String,
    val deviceID: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val password: String,
    val otp: String
)

data class AuthResponse(
    val message: String,
    val success: Boolean,
    val token: String? = null,
    val account: Account? = null,
    val action: String? = null,
    val email: String? = null,
    val data: AuthResponseData? = null
)

data class AuthResponseData(
    val email: String? = null,
    val deviceID: String? = null,
    val otherInfo: Map<String, Any>? = null
)

data class Account(
    val username: String? = null,
    val email: String? = null,
    val contactNumber: String? = null
) 