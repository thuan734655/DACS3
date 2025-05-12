package com.example.dacs3.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.AuthResponse
import com.example.dacs3.data.model.LoginRequest
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
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                action = "verify_email",
                                email = authResponse.account?.email
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                isError = true, 
                                errorMessage = authResponse?.message ?: "Registration failed"
                            )
                        }
                    }
                } else {
                    // Try to extract detailed error message from response body
                    val errorMessage = extractErrorMessage(response.errorBody())
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isError = true, 
                            errorMessage = errorMessage ?: "Registration failed: ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                // Network error - can't reach server
                Log.e("AuthViewModel", "Network error during registration", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isError = true, 
                        errorMessage = "Network error: Check your internet connection"
                    )
                }
            } catch (e: HttpException) {
                // HTTP error - server responded with an error
                Log.e("AuthViewModel", "HTTP error during registration: ${e.code()}", e)
                
                // Try to extract detailed error message from response body
                val errorMessage = extractErrorMessage(e.response()?.errorBody())
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isError = true, 
                        errorMessage = errorMessage ?: "Server error (${e.code()}): ${e.message()}"
                    )
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
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    handleLoginResponse(authResponse)
                } else {
                    // Try to extract detailed error message from response body
                    val errorBody = response.errorBody()
                    val errorString = errorBody?.string() ?: ""
                    Log.d("AuthViewModel", "Login error response: $errorString")
                    
                    // Check if this is an action response (verify_email or 2fa)
                    if (errorString.contains("\"action\":\"verify_email\"") || errorString.contains("Please verify your email")) {
                        try {
                            val jsonObject = JSONObject(errorString)
                            val emailFromResponse = jsonObject.optString("email", accountName)
                            
                            Log.d("AuthViewModel", "Detected verify_email requirement in error response")
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    isSuccess = false,
                                    isError = false, // Not an error in user flow context
                                    action = "verify_email",
                                    email = emailFromResponse
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error parsing verify_email response", e)
                            _uiState.update { 
                                it.copy(
                                    isLoading = false, 
                                    isError = true, 
                                    errorMessage = "Please verify your email before logging in"
                                )
                            }
                        }
                    } else if (errorString.contains("\"action\":\"2fa\"")) {
                        try {
                            val jsonObject = JSONObject(errorString)
                            val email = jsonObject.optString("email", accountName)
                            
                            Log.d("AuthViewModel", "Detected 2FA requirement in error response")
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    isSuccess = false,
                                    action = "2fa",
                                    email = email
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error parsing 2FA error response", e)
                            _uiState.update { 
                                it.copy(
                                    isLoading = false, 
                                    isError = true, 
                                    errorMessage = "Login failed: 2FA parsing error"
                                )
                            }
                        }
                    } else {
                        // Try to parse error directly from the errorString
                        try {
                            val jsonObject = JSONObject(errorString)
                            val message = jsonObject.optString("message", null)
                            val errorMessage = when {
                                message.contains("Invalid credentials") -> 
                                    "Invalid email or password. Please try again."
                                message.isNotEmpty() -> message
                                else -> "Login failed: ${response.message()}"
                            }
                            
                            _uiState.update { 
                                it.copy(
                                    isLoading = false, 
                                    isError = true, 
                                    errorMessage = errorMessage
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error parsing login error response", e)
                            _uiState.update { 
                                it.copy(
                                    isLoading = false, 
                                    isError = true, 
                                    errorMessage = errorString.ifBlank { "Login failed: ${response.message()}" }
                                )
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                // Network error - can't reach server, try offline login
                Log.e("AuthViewModel", "Network error during login, trying offline", e)
                tryOfflineLogin(accountName)
            } catch (e: HttpException) {
                // HTTP error - server responded with an error
                Log.e("AuthViewModel", "HTTP error during login: ${e.code()}", e)
                
                // Try to extract detailed error message from response body
                val errorMessage = extractErrorMessage(e.response()?.errorBody())
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isError = true, 
                        errorMessage = errorMessage ?: "Server error (${e.code()}): ${e.message()}"
                    )
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

    private suspend fun tryOfflineLogin(email: String) {
        try {
            val localUser = authRepository.getLocalUserByEmail(email)
            if (localUser != null) {
                // Found user locally, proceed with offline login
                sessionManager.saveUserSession(localUser.userId, localUser.email)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        isOfflineMode = true,
                        username = localUser.username,
                        email = localUser.email,
                        action = "login_success"
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isError = true, 
                        errorMessage = "Cannot login offline: Account not found locally"
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    isError = true, 
                    errorMessage = "Offline login failed: ${e.message}"
                )
            }
        }
    }

    private fun handleLoginResponse(authResponse: AuthResponse?) {
        // Log the response for debugging
        Log.d("AuthViewModel", "Handling login response: success=${authResponse?.success}, action=${authResponse?.action}, message=${authResponse?.message}")
        
        when {
            authResponse?.success == true -> {
                // Successful login
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        token = authResponse.token,
                        username = authResponse.account?.username,
                        action = "login_success"
                    )
                }
            }
            authResponse?.action == "verify_email" -> {
                // Need email verification
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        action = "verify_email",
                        email = authResponse.email
                    )
                }
            }
            authResponse?.action == "2fa" -> {
                // Need two-factor authentication
                Log.d("AuthViewModel", "2FA required from server, email: ${authResponse.email}")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        action = "2fa",
                        email = authResponse.email ?: authResponse.data?.email
                    )
                }
            }
            else -> {
                // Other error
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isError = true, 
                        errorMessage = authResponse?.message ?: "Login failed"
                    )
                }
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
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "", action = null) }
            
            try {
                val response = authRepository.forgotPassword(email)
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                action = "verify_otp_for_reset",
                                email = email
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                isError = true, 
                                errorMessage = authResponse?.message ?: "Password reset request failed"
                            )
                        }
                    }
                } else {
                    // Extract error message from response
                    val errorMessage = extractErrorMessage(response.errorBody())
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isError = true, 
                            errorMessage = errorMessage ?: "Password reset request failed: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
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
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                action = "password_reset_success",
                                email = email
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                isError = true, 
                                errorMessage = authResponse?.message ?: "Password reset failed"
                            )
                        }
                    }
                } else {
                    // Extract error message from response
                    val errorMessage = extractErrorMessage(response.errorBody())
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isError = true, 
                            errorMessage = errorMessage ?: "Password reset failed: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
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

    // Add function to request verification OTP
    fun requestVerificationOtp(email: String) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Requesting verification OTP for: $email")
                val response = otpRepository.resendOtp(email)
                
                if (response.isSuccessful) {
                    Log.d("AuthViewModel", "Verification OTP sent successfully")
                } else {
                    Log.e("AuthViewModel", "Failed to send verification OTP: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error requesting verification OTP", e)
            }
        }
    }
}
