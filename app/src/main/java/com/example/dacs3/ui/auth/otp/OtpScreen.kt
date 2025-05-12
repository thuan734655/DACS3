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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    email: String,
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onTwoFactorAuthRequired: (String) -> Unit = {},
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
    
    // Set email and action when screen loads
    LaunchedEffect(email, action) {
        viewModel.setEmail(email)
        
        // Set action if it's 2FA
        if (action == "2fa") {
            Log.d("OtpScreen", "Setting action to 2FA from parameter")
            viewModel.setAction("2fa")
        }
    }
    
    // Check for verification success or 2FA requirement
    LaunchedEffect(otpState.isSuccess, otpState.isError, otpState.requires2FA) {
        if (otpState.isSuccess) {
            // Ghi log để debug
            Log.d("OtpScreen", "Verification success! Action: ${otpState.action}")
            
            // Cập nhật trạng thái hiển thị thông báo thành công
            showSuccessMessage = true
            // Hiển thị dialog thành công
            showSuccessDialog = true
            
            // Không tự động chuyển màn hình, chờ người dùng đóng dialog
        } else if (otpState.requires2FA) {
            // Handle 2FA redirect
            Log.d("OtpScreen", "2FA required, additional data: ${otpState.additionalData}")
            
            // Get email from additional data or use current email
            val emailForVerification = otpState.additionalData?.get("email") as? String ?: email
            
            // Navigate to 2FA screen
            onTwoFactorAuthRequired(emailForVerification)
        } else if (otpState.isError) {
            Log.d("OtpScreen", "Verification error: ${otpState.errorMessage}")
            
            // Check if error message actually indicates success
            if (otpState.errorMessage.contains("success", ignoreCase = true)) {
                Log.d("OtpScreen", "Success message detected in error response")
                showSuccessMessage = true
                showSuccessDialog = true
                // Không tự động chuyển màn hình, chờ người dùng đóng dialog
            }
        }
    }
    
    // Hiển thị dialog thành công khi cần
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
                    "Xác thực thành công", 
                    textAlign = TextAlign.Center, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    "OTP đã được xác thực thành công. Bạn sẽ được chuyển đến trang đăng nhập.", 
                    textAlign = TextAlign.Center
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showSuccessDialog = false
                        onVerificationSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Đăng nhập")
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
                    "Xác thực thiết bị mới", 
                    textAlign = TextAlign.Center, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    "Bạn đang đăng nhập từ một thiết bị mới. Vui lòng kiểm tra email để xác nhận đăng nhập.",
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
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Đã hiểu")
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
            text = if (otpState.action == "2fa") "Two-Factor Authentication" else "Email Verification",
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
                            
                            // If all digits are filled, verify OTP
                            val allDigitsFilled = otpDigits.all { it.isNotEmpty() }
                            if (allDigitsFilled) {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                viewModel.verifyOtp(otpDigits.joinToString(""))
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
                    viewModel.verifyOtp(otpValue)
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
            enabled = !otpState.isLoading && !(showSuccessMessage || otpState.isSuccess)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Didn't receive the code? ",
                    fontSize = 14.sp
                )
                
                if (otpState.canResend) {
                    TextButton(
                        onClick = { viewModel.resendOtp() },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            text = "Resend Now",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "Resend in ${otpState.remainingSeconds}s",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
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