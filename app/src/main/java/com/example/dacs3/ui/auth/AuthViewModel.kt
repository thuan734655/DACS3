package com.example.dacs3.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.LoginRequest
import com.example.dacs3.data.model.LoginResponse
import com.example.dacs3.data.model.RegisterRequest
import com.example.dacs3.data.repository.AuthRepository
import com.example.dacs3.data.repository.OtpRepository
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import java.util.UUID

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val action: String? = null,
    val email: String? = null,
    val token: String? = null,
    val username: String? = null,
    val isOfflineMode: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val otpRepository: OtpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Create a state for the current user ID
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        // Check if already logged in from SessionManager
        if (sessionManager.isLoggedIn()) {
            _uiState.update {
                it.copy(
                    isSuccess = true,
                    action = "login_success",
                    email = sessionManager.getUserEmail(),
                    username = sessionManager.getUserEmail()?.substringBefore('@')
                )
            }
            // Set current user ID
            _currentUserId.value = sessionManager.getUserId()
        }
    }

    fun register(username: String, email: String, contactNumber: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }
            try {
                val request = RegisterRequest(username, email, contactNumber, password)
                val response = authRepository.register(request)

                if (response.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            action = "verify_email",
                            email = response.account?.email
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Register error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Registration failed: ${e.message}"
                    )
                }
            }
        }
    }

    private fun extractErrorMessage(errorBody: ResponseBody?): String? {
        errorBody?.let {
            try {
                val errorString = it.string()
                Log.d("AuthViewModel", "Error response: $errorString")

                // Check if errorString is empty
                if (errorString.isBlank()) {
                    return "Unknown server error"
                }

                try {
                    val jsonObject = JSONObject(errorString)
                    val message = jsonObject.optString("message", null)

                    return when {
                        message.contains("duplicate key error") && message.contains("username") ->
                            "Username already exists. Please choose another username."
                        message.contains("duplicate key error") && message.contains("email") ->
                            "Email already registered. Please use another email."
                        message.contains("Invalid credentials") ->
                            "Invalid email or password. Please try again."
                        message.isNotEmpty() -> message
                        else -> null
                    }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error parsing error response (Ask Gemini)", e)
                    // Return the raw error string if JSON parsing fails
                    return errorString
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error reading error response", e)
                return null
            }
        }
        return null
    }

    fun login(accountName: String, password: String, isEmail: Boolean, deviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "", action = null) }

            try {
                val type = if (isEmail) "E" else "S"
                val request = LoginRequest(accountName, password, type, deviceId)
                val response = authRepository.login(request)

                if (response.success) {
                    handleLoginResponse(response)
                } else {
                    // Check for specific actions in the response
                    if (response.action == "verify_email") {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                isError = false,
                                action = "verify_email",
                                email = accountName
                            )
                        }
                    } else if (response.action == "2fa") {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                action = "2fa",
                                email = accountName
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = response.message ?: "Login failed"
                            )
                        }
                    }
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Login error", e)
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorString = errorBody?.string() ?: ""
                    Log.d("AuthViewModel", "Error response body: $errorString")

                    try {
                        val jsonObject = JSONObject(errorString)
                        val message = jsonObject.optString("message", "")
                        val action = jsonObject.optString("action", null)

                        if (action == "2fa") {
                            // This is a 2FA request, not a real error
                            val data = jsonObject.optJSONObject("data")
                            val email = data?.optString("email", accountName) ?: accountName

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isError = false,
                                    action = "2fa",
                                    email = email
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isError = true,
                                    errorMessage = message.ifEmpty { "Login failed: ${e.message}" }
                                )
                            }
                        }
                    } catch (jsonException: Exception) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Login failed: ${e.message}"
                            )
                        }
                    }
                } catch (parseException: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Login failed: ${e.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Login failed: ${e.message}"
                    )
                }
            }
        }
    }

    private fun handleLoginResponse(response: LoginResponse) {
        if (response.success && response.token != null) {
            // Extract user information from response
            val email = response.account?.email ?: ""

            // Extract userId from JWT token or generate one
            val userId = try {
                val payload = response.token.split(".")[1]
                val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
                val decodedString = String(decodedBytes)
                val jsonObject = JSONObject(decodedString)
                jsonObject.optString("id", UUID.randomUUID().toString())
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error extracting user ID from token", e)
                UUID.randomUUID().toString()
            }

            // Set current user ID
            _currentUserId.value = userId

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true,
                    action = "login_success",
                    email = email,
                    username = email.substringBefore('@'),
                    token = response.token,
                    errorMessage = ""
                )
            }
        }
    }

    fun logOut() {
        sessionManager.clearSession()
        resetState()
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }

    // Method to check if user is logged in for navigation
    fun checkLoggedInStatus(): Boolean {
        val isLoggedIn = sessionManager.isLoggedIn()
        if (isLoggedIn) {
            _currentUserId.value = sessionManager.getUserId()
        }
        return isLoggedIn
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }

            try {
                val response = authRepository.forgotPassword(email)

                if (response.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            action = response.action ?: "reset_password",
                            email = response.email ?: email
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = response.message ?: "Failed to send reset code"
                        )
                    }
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Forgot password error", e)
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorString = errorBody?.string() ?: ""

                    try {
                        val jsonObject = JSONObject(errorString)
                        val message = jsonObject.optString("message", "")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = message.ifEmpty { "Password reset request failed: ${e.message}" }
                            )
                        }
                    } catch (jsonException: Exception) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Password reset request failed: ${e.message}"
                            )
                        }
                    }
                } catch (parseException: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Password reset request failed: ${e.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Forgot password error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Password reset request failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetPassword(email: String, password: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }

            try {
                val response = authRepository.resetPassword(email, password, otp)

                if (response.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            action = "password_reset_complete",
                            email = response.email ?: email
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = response.message ?: "Failed to reset password"
                        )
                    }
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Reset password error", e)
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorString = errorBody?.string() ?: ""

                    try {
                        val jsonObject = JSONObject(errorString)
                        val message = jsonObject.optString("message", "")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = message.ifEmpty { "Password reset failed: ${e.message}" }
                            )
                        }
                    } catch (jsonException: Exception) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Password reset failed: ${e.message}"
                            )
                        }
                    }
                } catch (parseException: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Password reset failed: ${e.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Reset password error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Password reset failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }

            try {
                val response = authRepository.resendOtp(email, true)

                if (response.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            email = response.email ?: email
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Resend OTP error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to resend verification code: ${e.message}"
                    )
                }
            }
        }
    }

    // Add function to request verification OTP
    fun requestVerificationOtp(email: String) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Requesting verification OTP for: $email")
                val response = authRepository.resendOtp(email, true)

                if (response.success) {
                    Log.d("AuthViewModel", "Verification OTP sent successfully")
                } else {
                    Log.e("AuthViewModel", "Failed to send verification OTP: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error requesting verification OTP", e)
            }
        }
    }
}