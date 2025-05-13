package com.example.dacs3.ui.auth.otp

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.OtpState
import com.example.dacs3.data.repository.AuthRepository
import com.example.dacs3.data.repository.OtpRepository
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.util.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpRepository: OtpRepository,
    private val authRepository: AuthRepository,
    private val deviceUtils: DeviceUtils,
    private val sessionManager: SessionManager,
    application: Application
) : AndroidViewModel(application) {

    private val _otpState = MutableStateFlow(OtpState())
    val otpState: StateFlow<OtpState> = _otpState.asStateFlow()
    
    private var countDownTimer: CountDownTimer? = null
    
    // Track last resend time to enforce cooldown
    private var lastResendTime: Long = 0
    
    fun setEmail(email: String, action: String? = null) {
        if (email.isBlank()) return
        
        // Update email state
        _otpState.update { 
            it.copy(
                email = email,
                remainingSeconds = 60,
                canResend = false
            )
        }
        
        // Set action if provided
        if (action == "2fa" || action == "reset_password") {
            setAction(action)
        }
        
        // Start cooldown timer on initial load to prevent immediate resend
        lastResendTime = System.currentTimeMillis()
        startCountdown()
    }
    
    fun verifyOtp(otp: String) {
        val email = _otpState.value.email
        if (email.isBlank()) {
            _otpState.update { 
                it.copy(
                    isError = true,
                    errorMessage = "Email address is missing"
                )
            }
            return
        }
        
        if (!OtpValidationUtils.isValidOtp(otp)) {
            _otpState.update { 
                it.copy(
                    isError = true,
                    errorMessage = "OTP must be 6 digits"
                )
            }
            return
        }
        
        viewModelScope.launch {
            _otpState.update { 
                it.copy(
                    isLoading = true, 
                    isError = false, 
                    errorMessage = ""
                )
            }
            
            try {
                val action = _otpState.value.action
                val deviceId = if (action == "2fa") deviceUtils.getDeviceId() else null
                
                val response = otpRepository.verifyOtp(email, otp, deviceId)
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = ""
                            )
                        }
                    } else {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = authResponse?.message ?: "Verification failed"
                            )
                        }
                    }
                } else {
                    _otpState.update { 
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Failed to verify OTP: ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Network error, please check your connection"
                    )
                }
            } catch (e: HttpException) {
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Server error (${e.code()}): ${e.message()}"
                    )
                }
            } catch (e: Exception) {
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun resendOtp() {
        val email = _otpState.value.email
        if (email.isBlank()) {
            _otpState.update { 
                it.copy(
                    isError = true,
                    errorMessage = "Email address is missing"
                )
            }
            return
        }
        
        // Allow resending immediately for 2FA, regardless of timer
        val is2fa = _otpState.value.action == "2fa"
        
        // Check if enough time has passed since last resend (60 seconds)
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastResendTime
        
        // Strict enforcement: Don't allow resend until full 60 seconds have passed
        if (!is2fa && elapsedTime < 60000) {
            // Calculate remaining seconds and update UI
            val remainingMs = 60000 - elapsedTime
            val remainingSec = (remainingMs / 1000).toInt() + 1 // Round up
            
            _otpState.update {
                it.copy(
                    canResend = false,
                    remainingSeconds = remainingSec
                )
            }
            return
        }
        
        // Cancel any existing timer
        countDownTimer?.cancel()
        
        // Update resend timestamp immediately
        lastResendTime = currentTime
        
        // Enforce cooldown immediately
        _otpState.update { 
            it.copy(
                canResend = false,
                remainingSeconds = 60
            )
        }
        
        // Start the countdown
        startCountdown()
        
        viewModelScope.launch {
            _otpState.update { 
                it.copy(
                    isLoading = true, 
                    isError = false // Always reset error on resend
                )
            }
            
            try {
                val response = otpRepository.resendOtp(email)
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isError = false,
                                errorMessage = ""
                            )
                        }
                    } else {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = authResponse?.message ?: "Failed to resend OTP"
                                // Keep canResend false to enforce waiting period regardless of success
                            )
                        }
                    }
                } else {
                    _otpState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to resend OTP: ${response.message()}"
                            // Keep canResend false to enforce waiting period regardless of success
                        )
                    }
                }
            } catch (e: Exception) {
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                        // Keep canResend false to enforce waiting period regardless of success
                    )
                }
            }
        }
    }
    
    private fun startCountdown() {
        // Cancel any existing timer
        countDownTimer?.cancel()
        
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                _otpState.update { 
                    it.copy(
                        remainingSeconds = secondsRemaining,
                        canResend = false // Always ensure canResend is false during countdown
                    )
                }
            }
            
            override fun onFinish() {
                // Calculate time since last resend
                val timeSinceResend = System.currentTimeMillis() - lastResendTime
                
                // Only allow resend if full 60 seconds have passed
                if (timeSinceResend >= 60000) {
                    _otpState.update { 
                        it.copy(
                            remainingSeconds = 0,
                            canResend = true
                        )
                    }
                } else {
                    // If less than 60 seconds have passed, restart countdown with remaining time
                    val remainingMs = 60000 - timeSinceResend
                    _otpState.update {
                        it.copy(
                            remainingSeconds = (remainingMs / 1000).toInt() + 1, // Round up
                            canResend = false
                        )
                    }
                    
                    // Start new timer with remaining time
                    countDownTimer?.cancel()
                    countDownTimer = object : CountDownTimer(remainingMs, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val seconds = (millisUntilFinished / 1000).toInt()
                            _otpState.update {
                                it.copy(
                                    remainingSeconds = seconds,
                                    canResend = false
                                )
                            }
                        }
                        
                        override fun onFinish() {
                            _otpState.update {
                                it.copy(
                                    remainingSeconds = 0,
                                    canResend = true
                                )
                            }
                        }
                    }.start()
                }
            }
        }.start()
    }
    
    fun clearError() {
        _otpState.update { 
            it.copy(
                isError = false, 
                errorMessage = ""
            )
        }
    }
    
    private fun setAction(action: String) {
        _otpState.update { 
            it.copy(action = action)
        }
    }
    
    fun verifyEmail(email: String, otp: String) {
        viewModelScope.launch {
            _otpState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }
            try {
                val response = authRepository.verifyEmail(email, otp)
                if (response.isSuccessful && response.body()?.success == true) {
                    _otpState.update { it.copy(isLoading = false, isSuccess = true, errorMessage = "", action = "email_verified") }
                } else {
                    _otpState.update { it.copy(isLoading = false, isError = true, errorMessage = response.body()?.message ?: "Verification failed") }
                }
            } catch (e: Exception) {
                _otpState.update { it.copy(isLoading = false, isError = true, errorMessage = "Verification failed: ${e.message}") }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
} 