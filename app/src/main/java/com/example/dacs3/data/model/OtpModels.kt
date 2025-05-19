package com.example.dacs3.data.model

import com.example.dacs3.data.model.VerifyEmailRequest

// OTP Verification Requests
data class OtpVerificationRequest(
    val email: String,
    val otp: String,
    val purpose: String // "verify_email", "reset_password", "2fa"
)

// OTP Resend Request
data class OtpResendRequest(
    val email: String,
    val purpose: String // "verify_email", "reset_password", "2fa"
)

// OTP Responses
data class OtpVerificationResponse(
    val success: Boolean,
    val message: String,
    val data: OtpResponseData?
)

data class OtpResendResponse(
    val success: Boolean,
    val message: String,
    val email: String?
)

data class OtpResponseData(
    val email: String,
    val verified: Boolean,
    val token: String?
)

data class OtpState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val email: String = "",
    val password: String = "",  // Added to store password for auto-login
    val action: String? = null,
    val remainingSeconds: Int = 60,
    val canResend: Boolean = false,
    val requires2FA: Boolean = false,
    val additionalData: Map<String, Any>? = null,
    val isAutoLoginSuccessful: Boolean = false  // Flag to indicate auto-login status
)

// VerifyEmailRequest is imported from CommonModels.kt