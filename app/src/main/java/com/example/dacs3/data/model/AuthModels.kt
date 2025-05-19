package com.example.dacs3.data.model

import com.example.dacs3.data.model.VerifyEmailRequest

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

// VerifyEmailRequest is imported from CommonModels.kt

data class ResendOtpRequest(
    val email: String,
    val forVerification: Boolean
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String,
    val deviceID: String
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

data class RegisterResponse(
    val message: String,
    val success: Boolean,
    val account: AccountInfo?
)

data class LoginResponse(
    val message: String,
    val token: String?,
    val success: Boolean,
    val account: AccountInfo?,
    val action: String?
)

data class ForgotPasswordResponse(
    val message: String,
    val success: Boolean,
    val email: String?,
    val action: String?
)

data class ResetPasswordResponse(
    val message: String,
    val success: Boolean,
    val email: String?
)

data class VerifyEmailResponse(
    val message: String,
    val success: Boolean,
    val email: String?
)

data class ResendOtpResponse(
    val message: String,
    val success: Boolean,
    val email: String?
)

data class VerifyOtpResponse(
    val message: String,
    val success: Boolean,
    val token: String?
)

data class AccountInfo(
    val userId: String,
    val username: String,
    val email: String,
    val contactNumber: String
) 