package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.models.LoginRequest
import com.example.dacs3.models.LoginResponse
import com.example.dacs3.models.RegisterRequest
import com.example.dacs3.models.RegisterResponse
import com.example.dacs3.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import org.json.JSONObject

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class SuccessRegister(
        val response: RegisterResponse,
        val message: String = response.message
    ) : AuthState()
    data class SuccessLogin(
        val response: LoginResponse,
        val message: String = response.message
    ) : AuthState()
    data class Error(
        val message: String
    ) : AuthState()
    data class VerifyEmail(
        val email: String,
        val message: String = "Please verify your email address"
    ) : AuthState()
    data class TwoFactorAuth(
        val email: String,
        val message: String = "Please enter the 2FA code sent to your email"
    ) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.register(request)
                handleAuthResponseRegister(response)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.login(request)
                handleAuthResponseLogin(response)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun handleAuthResponseRegister(response: RegisterResponse) {
        when {
            response.error != null -> {
                _authState.value = AuthState.Error(response.error)
            }

            response.action == "verify_email" -> {
                _authState.value = AuthState.VerifyEmail(
                    email = response.account?.email ?: "",
                    message = response.message
                )
            }
            response.action == "2fa" -> {
                _authState.value = AuthState.TwoFactorAuth(
                    email = response.account?.email ?: "",
                    message = response.message
                )
            }
            response.success == true -> {
                _authState.value = AuthState.SuccessRegister(response)
            }
        }
    }

    private suspend fun handleAuthResponseLogin(response: LoginResponse) {
        when {
            response.error != null -> {
                _authState.value = AuthState.Error(response.error)
            }
            response.success == true -> {
                _authState.value = AuthState.SuccessLogin(response)
            }
            response.action == "verify_email" -> {
                _authState.value = AuthState.VerifyEmail(
                    email = response.account?.email ?: "",
                    message = response.message
                )
            }
            response.action == "2fa" -> {
                _authState.value = AuthState.TwoFactorAuth(
                    email = response.account?.email ?: "",
                    message = response.message
                )
            }
            response.token != null -> {
                _authState.value = AuthState.SuccessLogin(
                    response = response,
                    message = response.message
                )
            }
        }
    }

    private fun handleError(e: Exception) {
        val errorMessage = when (e) {
            is HttpException -> {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        // Try to parse the error message from the response
                        try {
                            val json = JSONObject(errorBody)
                            json.optString("message", errorBody)
                        } catch (ex: Exception) {
                            errorBody
                        }
                    } else {
                        when (e.code()) {
                            400 -> "Invalid request. Please check your input."
                            401 -> "Authentication failed. Please check your credentials."
                            403 -> "Access denied. Please try again later."
                            404 -> "Service not found. Please try again later."
                            500 -> "Server error. Please try again later."
                            else -> "Network error occurred. Please try again."
                        }
                    }
                } catch (ex: Exception) {
                    when (e.code()) {
                        400 -> "Invalid request. Please check your input."
                        401 -> "Authentication failed. Please check your credentials."
                        403 -> "Access denied. Please try again later."
                        404 -> "Service not found. Please try again later."
                        500 -> "Server error. Please try again later."
                        else -> "Network error occurred. Please try again."
                    }
                }
            }
            is IOException -> "Network connection error. Please check your internet connection."
            else -> e.message ?: "An unexpected error occurred. Please try again."
        }
        _authState.value = AuthState.Error(errorMessage)
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Initial
        }
    }
}