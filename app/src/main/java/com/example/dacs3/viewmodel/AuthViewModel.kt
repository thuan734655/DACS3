package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.network.LoginRequest
import com.example.dacs3.network.LoginResponse
import com.example.dacs3.network.RegisterRequest
import com.example.dacs3.network.RegisterResponse
import com.example.dacs3.network.ResendOtpResponse
import com.example.dacs3.network.VerifyOtpResponse
import com.example.dacs3.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<UiState<RegisterResponse>>(UiState.Idle)
    val registerState: StateFlow<UiState<RegisterResponse>> = _registerState

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

    private val _resendState = MutableStateFlow<UiState<ResendOtpResponse>>(UiState.Idle)
    val resendState: StateFlow<UiState<ResendOtpResponse>> = _resendState

    private val _verifyState = MutableStateFlow<UiState<VerifyOtpResponse>>(UiState.Idle)
    val verifyState: StateFlow<UiState<VerifyOtpResponse>> = _verifyState


    fun register(req: RegisterRequest) = viewModelScope.launch {
        _registerState.value = UiState.Loading
        repo.register(req).fold(
            onSuccess = { _registerState.value = UiState.Success(it) },
            onFailure = { _registerState.value = UiState.Error(it.message ?: "Lỗi đăng ký") }
        )
    }

    fun login(req: LoginRequest) = viewModelScope.launch {
        _loginState.value = UiState.Loading
        repo.login(req).fold(
            onSuccess = { _loginState.value = UiState.Success(it) },
            onFailure = { _loginState.value = UiState.Error(it.message ?: "Lỗi đăng nhập") }
        )
    }

    fun resendOtp(email: String) = viewModelScope.launch {
        _resendState.value = UiState.Loading
        repo.resendOtp(email).fold(
            onSuccess={ _resendState.value = UiState.Success(it) },
            onFailure={ _resendState.value = UiState.Error(it.message ?: "Error") }
        )
    }

    fun verifyOtp(email: String, otp: String) = viewModelScope.launch {
        _verifyState.value = UiState.Loading
        repo.verifyOtp(email, otp).fold(
            onSuccess={ _verifyState.value = UiState.Success(it) },
            onFailure={ _verifyState.value = UiState.Error(it.message ?: "Error") }
        )
    }
}

