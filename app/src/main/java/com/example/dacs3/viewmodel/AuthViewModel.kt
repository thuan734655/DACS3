package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.models.LoginRequest
import com.example.dacs3.models.LoginResponse
import com.example.dacs3.models.RegisterRequest
import com.example.dacs3.models.RegisterResponse
import com.example.dacs3.models.ResendOtpResponse
import com.example.dacs3.models.VerifyOtpResponse
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

}

