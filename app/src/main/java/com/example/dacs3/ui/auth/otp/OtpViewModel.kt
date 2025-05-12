package com.example.dacs3.ui.auth.otp

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.OtpState
import com.example.dacs3.data.repository.OtpRepository
import com.example.dacs3.data.session.SessionManager
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
    private val sessionManager: SessionManager,
    application: Application
) : AndroidViewModel(application) {

    private val _otpState = MutableStateFlow(OtpState())
    val otpState: StateFlow<OtpState> = _otpState.asStateFlow()
    
    private var countDownTimer: CountDownTimer? = null
    
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
        if (action == "2fa") {
            setAction("2fa")
        }
        
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
            _otpState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }
            
            try {
                // Get device ID for verification using application context directly from Settings
                val deviceId = android.provider.Settings.Secure.getString(
                    getApplication<Application>().contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )
                // Log the device ID for debugging
                Log.d("OtpViewModel", "Using direct Android ID for OTP verification: $deviceId")
                
                val response = otpRepository.verifyOtp(email, otp, deviceId)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    // Check for successful message regardless of success flag
                    if (body?.success == true || body?.message?.contains("successfully", ignoreCase = true) == true) {
                        countDownTimer?.cancel()
                        Log.d("OtpViewModel", "OTP verification successful: ${body.message}")
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isError = false,
                                errorMessage = "",
                                action = "verification_success"
                            )
                        }
                    } else if (body?.action == "2fa") {
                        // Handle 2FA requirement
                        Log.d("OtpViewModel", "2FA required: ${body.message}")
                        
                        // Extract data from response
                        val dataMap = mutableMapOf<String, Any>()
                        body.data?.let { responseData ->
                            // Add email if present
                            responseData.email?.let { email ->
                                dataMap["email"] = email
                            }
                            // Add any other fields that might be in the response
                        }
                        
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isError = false,
                                requires2FA = true,
                                errorMessage = body.message ?: "Device verification required",
                                action = "2fa",
                                additionalData = dataMap
                            )
                        }
                    } else {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = body?.message ?: "Verification failed",
                                action = body?.action
                            )
                        }
                    }
                } else {
                    _otpState.update { 
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Error: ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("OtpViewModel", "Network error during OTP verification", e)
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Network error. Please check your connection."
                    )
                }
            } catch (e: HttpException) {
                Log.e("OtpViewModel", "HTTP error during OTP verification: ${e.code()}", e)
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Server error (${e.code()}): ${e.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("OtpViewModel", "Error verifying OTP", e)
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Verification failed: ${e.message}"
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
        
        if (!_otpState.value.canResend) {
            _otpState.update { 
                it.copy(
                    isError = true,
                    errorMessage = "Please wait before requesting another OTP"
                )
            }
            return
        }
        
        viewModelScope.launch {
            _otpState.update { 
                it.copy(
                    isLoading = true, 
                    isError = false, 
                    errorMessage = "",
                    remainingSeconds = 60,
                    canResend = false
                )
            }
            
            try {
                val response = otpRepository.resendOtp(email)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true || body?.action == "enter_otp") {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "OTP has been sent to your email"
                            )
                        }
                        startCountdown()
                    } else {
                        _otpState.update { 
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = body?.message ?: "Failed to resend OTP"
                            )
                        }
                    }
                } else {
                    _otpState.update { 
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Error: ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("OtpViewModel", "Network error during OTP resend", e)
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Network error. Please check your connection."
                    )
                }
            } catch (e: HttpException) {
                Log.e("OtpViewModel", "HTTP error during OTP resend: ${e.code()}", e)
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Server error (${e.code()}): ${e.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("OtpViewModel", "Error resending OTP", e)
                _otpState.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to resend OTP: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun startCountdown() {
        // Cancel any existing timer
        countDownTimer?.cancel()
        
        countDownTimer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1_000
                _otpState.update { 
                    it.copy(
                        remainingSeconds = secondsRemaining.toInt(),
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
    
    fun clearError() {
        _otpState.update {
            it.copy(
                isError = false,
                errorMessage = ""
            )
        }
    }
    
    // Function to set the action manually
    fun setAction(action: String?) {
        _otpState.update {
            it.copy(
                action = action
            )
        }
        
        // If setting to 2FA, update requires2FA flag as well
        if (action == "2fa") {
            _otpState.update {
                it.copy(
                    requires2FA = true
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
} 