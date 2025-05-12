package com.example.dacs3.data.model

data class OtpVerificationRequest(
    val email: String,
    val otp: String,
    val deviceID: String? = null
)

data class OtpResendRequest(
    val email: String
)

data class OtpState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val email: String = "",
    val action: String? = null,
    val remainingSeconds: Int = 60,
    val canResend: Boolean = false,
    val requires2FA: Boolean = false,
    val additionalData: Map<String, Any>? = null
)

data class VerifyEmailRequest(
    val email: String,
    val otp: String
) 