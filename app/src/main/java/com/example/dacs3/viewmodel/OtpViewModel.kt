package com.example.dacs3.viewmodel;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.models.ResendOtpResponse
import com.example.dacs3.models.VerifyOtpResponse
import com.example.dacs3.repository.AuthRepository
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
public class OtpViewModel @Inject constructor (
    private val repo: AuthRepository
) : ViewModel() {

    private val _resendState = MutableStateFlow<UiState<ResendOtpResponse>>(UiState.Idle)
    val resendState: StateFlow<UiState<ResendOtpResponse>> = _resendState

    private val _verifyState = MutableStateFlow<UiState<VerifyOtpResponse>>(UiState.Idle)
    val verifyState: StateFlow<UiState<VerifyOtpResponse>> = _verifyState

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
