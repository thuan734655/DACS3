package com.example.dacs3.ui.auth.otp

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.example.dacs3.ui.theme.TeamNexusPurple

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    email: String,
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onTwoFactorAuthRequired: (String) -> Unit = {},
    onResetPassword: (String, String) -> Unit = { _, _ -> },
    action: String? = null,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val otpState by viewModel.otpState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }
    val otpValue = otpDigits.joinToString("")
    
    // Trạng thái để theo dõi việc hiển thị thông báo thành công
    var showSuccessMessage by remember { mutableStateOf(false) }
    // Trạng thái để kiểm soát hiển thị dialog thành công
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Variable to control 2FA dialog display
    var showTwoFactorAuthDialog by remember { mutableStateOf(false) }
    
    // Thêm biến để kiểm soát dialog resend OTP
    var showResendDialog by remember { mutableStateOf(false) }
    var resendMessage by remember { mutableStateOf("") }
    
    // Set email and action when screen loads
    LaunchedEffect(email, action) {
        viewModel.setEmail(email, action)
        
        // Nếu là 2FA, tự động request OTP khi màn hình hiển thị
        if (action == "2fa") {
            Log.d("OtpScreen", "Auto requesting OTP for 2FA authentication")
            viewModel.resendOtp()
            // Hiển thị dialog thông báo đã gửi OTP
            resendMessage = "2FA verification code has been sent to your email. Please check your inbox."
            showResendDialog = true
        }
    }
    
    // Check for verification success or 2FA requirement
    LaunchedEffect(otpState.isSuccess, otpState.isError) {
        if (otpState.isSuccess) {
            // Ghi log để debug
            Log.d("OtpScreen", "Verification success! Action: ${otpState.action}")
            
            // Cập nhật trạng thái hiển thị thông báo thành công
            showSuccessMessage = true
            
            // Handle navigation based on action
            when (otpState.action) {
                "2fa" -> {
                    onTwoFactorAuthRequired(email)
                }
                "reset_password" -> {
                    delay(200) // Reduced delay for better UX
                    // Chuyển trực tiếp đến trang đặt lại mật khẩu
                    onResetPassword(email, otpValue)
                    // Không hiển thị dialog thành công
                }
                else -> {
                    // Hiển thị dialog thành công cho các trường hợp khác (verification email)
                    showSuccessDialog = true
                }
            }
        } else if (otpState.isError) {
            Log.d("OtpScreen", "Verification error: ${otpState.errorMessage}")
            
            // Check if error message actually indicates success
            if (otpState.errorMessage.contains("success", ignoreCase = true)) {
                Log.d("OtpScreen", "Success message detected in error response")
                showSuccessMessage = true
                
                // Nếu là reset_password, chuyển đến trang đặt lại mật khẩu
                if (otpState.action == "reset_password") {
                    delay(200) // Reduced delay
                    onResetPassword(email, otpValue)
                } else {
                    showSuccessDialog = true
                }
            }
        }
    }
    
    // Success dialog for email verification
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onVerificationSuccess()
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF34A853),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { 
                Text(
                    "Verification Successful", 
                    textAlign = TextAlign.Center, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    text = when(otpState.action) {
                        "reset_password" -> "OTP has been verified successfully. Please set your new password."
                        "email_verified" -> "Your email has been verified successfully. You will be redirected to the login screen."
                        else -> "OTP has been verified successfully. You will be redirected to the login screen."
                    },
                    textAlign = TextAlign.Center
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showSuccessDialog = false
                        if (otpState.action == "reset_password") {
                            onResetPassword(email, otpValue)
                        } else {
                            onVerificationSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TeamNexusPurple
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (otpState.action == "reset_password") "Reset Password" else "Login")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    
    // Thêm dialog thông báo resend OTP thành công
    if (showResendDialog) {
        AlertDialog(
            onDismissRequest = { 
                showResendDialog = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { 
                Text(
                    "OTP Resent", 
                    textAlign = TextAlign.Center, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    text = resendMessage.ifEmpty { "OTP has been resent to your email. Please check your inbox." },
                    textAlign = TextAlign.Center
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showResendDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TeamNexusPurple
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    
    // Show 2FA dialog when needed
    if (showTwoFactorAuthDialog) {
        AlertDialog(
            onDismissRequest = {
                showTwoFactorAuthDialog = false
                // Do not navigate away, user needs to complete 2FA
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = "Security",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { 
                Text(
                    "New Device Authentication", 
                    textAlign = TextAlign.Center, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    "You are logging in from a new device. Please check your email to confirm login.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Redirect to 2FA screen or handle in-place
                        showTwoFactorAuthDialog = false
                        // Implement 2FA flow - could be via a different screen
                        // For now, just ask them to try again with the OTP
                        viewModel.clearError()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TeamNexusPurple
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Understood")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header - conditionally change the title based on the action
        Text(
            text = when(otpState.action) {
                "2fa" -> "Two-Factor Authentication"
                "reset_password" -> "Password Reset Verification"
                else -> "Email Verification"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 16.dp)
        )
        
        // Instruction text
        Text(
            text = "Enter the 6-digit code sent to",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Email display
        Text(
            text = email,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // OTP Input Fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 6) {
                OtpDigitInput(
                    value = otpDigits[i],
                    onValueChange = { newValue ->
                        // Only accept single digits
                        if (newValue.length <= 1 && (newValue.isEmpty() || newValue[0].isDigit())) {
                            val newDigits = otpDigits.toMutableList().apply {
                                set(i, newValue)
                            }
                            otpDigits = newDigits
                            
                            // Auto-move focus to next field
                            if (newValue.isNotEmpty() && i < 5) {
                                focusManager.moveFocus(FocusDirection.Next)
                            } else if (newValue.isEmpty() && i > 0) {
                                focusManager.moveFocus(FocusDirection.Previous)
                            }
                            
                            // If all digits are filled, verify OTP or email
                            val allDigitsFilled = otpDigits.all { it.isNotEmpty() }
                            if (allDigitsFilled) {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                if (action == "verify_email" || otpState.action == "verify_email") {
                                    viewModel.verifyEmail(email, otpDigits.joinToString(""))
                                } else {
                                    viewModel.verifyOtp(otpDigits.joinToString(""))
                                }
                            }
                        }
                    },
                    isLastField = i == 5
                )
            }
        }
        
        // Success message - sử dụng biến showSuccessMessage hoặc otpState.isSuccess
        AnimatedVisibility(visible = showSuccessMessage || otpState.isSuccess) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OTP verified successfully",
                        color = Color(0xFF34A853),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Error message - đảm bảo không hiển thị khi đã thành công
        AnimatedVisibility(visible = otpState.isError && !(showSuccessMessage || otpState.isSuccess) && !otpState.errorMessage.contains("success", ignoreCase = true)) {
            Text(
                text = otpState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Verify button
        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                if (otpValue.length == 6) {
                    if (action == "verify_email" || otpState.action == "verify_email") {
                        viewModel.verifyEmail(email, otpValue)
                    } else {
                        viewModel.verifyOtp(otpValue)
                    }
                } else {
                    // Show error if OTP is incomplete
                    viewModel.clearError()
                    viewModel.verifyOtp(otpValue) // This will trigger validation error
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 32.dp),
            enabled = !otpState.isLoading && !(showSuccessMessage || otpState.isSuccess),
            colors = ButtonDefaults.buttonColors(
                containerColor = TeamNexusPurple
            )
        ) {
            if (otpState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Verify")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Resend OTP section - hide when verification is successful
        AnimatedVisibility(visible = !(showSuccessMessage || otpState.isSuccess)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Didn't receive the code?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (otpState.canResend) {
                    Button(
                        onClick = { 
                            if (otpState.canResend) { // Double-check before proceeding
                                viewModel.resendOtp()
                                // Hiển thị dialog resend thành công
                                resendMessage = "OTP has been resent to your email. Please check your inbox."
                                showResendDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TeamNexusPurple,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        enabled = otpState.canResend && !otpState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(40.dp)
                    ) {
                        Text("Resend Code")
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth(0.7f)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Wait ${otpState.remainingSeconds}s to resend",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TeamNexusPurple
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpDigitInput(
    value: String,
    onValueChange: (String) -> Unit,
    isLastField: Boolean
) {
    val focusManager = LocalFocusManager.current
    
    val backgroundColor = if (value.isNotEmpty()) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(48.dp)
            .aspectRatio(1f)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (value.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = if (isLastField) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) },
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
} 