package com.example.dacs3.ui.auth.twofactor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.LoginRequest
import com.example.dacs3.data.model.OtpState
import com.example.dacs3.data.repository.AuthRepository
import com.example.dacs3.data.repository.OtpRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TwoFactorAuthViewModel @Inject constructor(
    private val otpRepository: OtpRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(OtpState())
    val state: StateFlow<OtpState> = _state.asStateFlow()

    // Store password for auto login after OTP verification
    private var tempPassword: String? = null
    private var isAutoLoginEnabled: Boolean = false

    fun setEmail(email: String) {
        if (email.isBlank() || email == _state.value.email) return

        _state.update {
            it.copy(email = email)
        }
    }

    fun enableAutoLogin(password: String) {
        tempPassword = password
        isAutoLoginEnabled = true
    }

    fun verifyOtp(otp: String) {
        val email = _state.value.email
        if (email.isBlank()) {
            _state.update {
                it.copy(
                    isError = true,
                    errorMessage = "Email address is missing"
                )
            }
            return
        }

        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            _state.update {
                it.copy(
                    isError = true,
                    errorMessage = "OTP must be 6 digits"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false, errorMessage = "") }

            try {
                // Get the device ID
                val deviceId = android.provider.Settings.Secure.getString(
                    getApplication<Application>().contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )

                // Use the OTP repository to verify the code
                val response = otpRepository.verifyOtp(email, otp, deviceId)

                if (response.success) {
                    // Try auto-login if enabled
                    if (isAutoLoginEnabled && tempPassword != null) {
                        performAutoLogin(email, tempPassword!!, deviceId)
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isError = false
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = response.message
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("TwoFactorAuthViewModel", "Network error during OTP verification", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Network error. Please check your connection."
                    )
                }
            } catch (e: HttpException) {
                Log.e("TwoFactorAuthViewModel", "HTTP error during OTP verification: ${e.code()}", e)
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorString = errorBody?.string() ?: ""
                    Log.d("TwoFactorAuthViewModel", "Error response body: $errorString")

                    try {
                        val jsonObject = org.json.JSONObject(errorString)
                        val message = jsonObject.optString("message", "")

                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = if (message.isNotEmpty()) message else "Verification failed (${e.code()})"
                            )
                        }
                    } catch (jsonException: Exception) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Verification failed: ${e.message()}"
                            )
                        }
                    }
                } catch (parseException: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Verification failed: ${e.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TwoFactorAuthViewModel", "Error verifying OTP", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error verifying OTP: ${e.message}"
                    )
                }
            }
        }
    }

    // This function is kept for backward compatibility but is now obsolete
    fun checkVerificationStatus() {
        // Now we'll just show an error message since we need the OTP
        _state.update {
            it.copy(
                isError = true,
                errorMessage = "Please enter the 6-digit OTP sent to your email"
            )
        }
    }

    private fun performAutoLogin(email: String, password: String, deviceId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = "Logging in automatically...") }

            try {
                // Create login request - assuming email login
                val loginRequest = LoginRequest(email, password, "E", deviceId)
                val response = authRepository.login(loginRequest)

                if (response.success) {
                    // Login successful
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            isError = false,
                            errorMessage = ""
                        )
                    }
                } else {
                    // Login failed but OTP was verified
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            isError = true,
                            errorMessage = "OTP verified but login failed: ${response.message ?: "Unknown error"}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TwoFactorAuthViewModel", "Auto-login error", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,  // OTP was still verified
                        isError = true,
                        errorMessage = "OTP verified but login failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun resendVerificationEmail() {
        val email = _state.value.email
        if (email.isBlank()) {
            _state.update {
                it.copy(
                    isError = true,
                    errorMessage = "Email address is missing"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false, errorMessage = "") }

            try {
                // Call the API to resend the verification email with OTP
                Log.d("TwoFactorAuthViewModel", "Requesting OTP for email: $email")
                val response = otpRepository.resendOtp(email, forVerification = false)

                if (response.success) {
                    Log.d("TwoFactorAuthViewModel", "OTP requested successfully")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "OTP code sent. Please check your email."
                        )
                    }
                } else {
                    Log.e("TwoFactorAuthViewModel", "Failed to request OTP: ${response.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Failed to send OTP code: ${response.message}"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("TwoFactorAuthViewModel", "Network error resending OTP", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Network error. Please check your connection."
                    )
                }
            } catch (e: HttpException) {
                Log.e("TwoFactorAuthViewModel", "HTTP error resending OTP: ${e.code()}", e)
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorString = errorBody?.string() ?: ""
                    Log.d("TwoFactorAuthViewModel", "Error response body: $errorString")

                    try {
                        val jsonObject = org.json.JSONObject(errorString)
                        val message = jsonObject.optString("message", "")
                        val action = jsonObject.optString("action", null)

                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = if (message.isNotEmpty()) message else "Verification failed (${e.code()})"
                            )
                        }
                    } catch (jsonException: Exception) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Verification failed: ${e.message()}"
                            )
                        }
                    }
                } catch (parseException: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Verification failed: ${e.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TwoFactorAuthViewModel", "Error resending OTP", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error sending OTP code: ${e.message}"
                    )
                }
            }
        }
    }
} 