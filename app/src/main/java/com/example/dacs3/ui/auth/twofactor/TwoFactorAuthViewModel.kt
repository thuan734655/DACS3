package com.example.dacs3.ui.auth.twofactor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.repository.AuthRepository
import com.example.dacs3.data.repository.OtpRepository
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

data class TwoFactorAuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val email: String = ""
)

@HiltViewModel
class TwoFactorAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val otpRepository: OtpRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(TwoFactorAuthState())
    val state: StateFlow<TwoFactorAuthState> = _state.asStateFlow()
    
    fun setEmail(email: String) {
        if (email.isBlank() || email == _state.value.email) return
        
        _state.update { 
            it.copy(email = email)
        }
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
                // Call API to verify OTP
                val deviceId = android.provider.Settings.Secure.getString(
                    getApplication<Application>().contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )
                
                // Use the OTP repository to verify the code
                val response = otpRepository.verifyOtp(email, otp, deviceId)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true || body?.message?.contains("successfully", ignoreCase = true) == true) {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isError = false
                            )
                        }
                        
                        // Update local device verification status
                        authRepository.updateDeviceVerification(email, true)
                    } else {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = body?.message ?: "OTP verification failed"
                            )
                        }
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "OTP verification failed: ${response.message()}"
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
                _state.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Server error (${e.code()}): ${e.message()}"
                    )
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
                val response = otpRepository.resendOtp(email)
                
                if (response.isSuccessful) {
                    Log.d("TwoFactorAuthViewModel", "OTP requested successfully")
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "OTP code sent. Please check your email."
                        )
                    }
                } else {
                    Log.e("TwoFactorAuthViewModel", "Failed to request OTP: ${response.message()}")
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Failed to send OTP code: ${response.message()}"
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
                _state.update { 
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Server error (${e.code()}): ${e.message()}"
                    )
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