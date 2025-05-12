package com.example.dacs3.ui.auth.twofactor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TwoFactorAuthScreen(
    email: String,
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TwoFactorAuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }
    val otpValue = otpDigits.joinToString("")
    
    // Set email when screen loads and request OTP
    LaunchedEffect(Unit) {
        viewModel.setEmail(email)
        // Request OTP from server when the screen is shown
        viewModel.resendVerificationEmail()
    }
    
    // Handle verification success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            delay(1500)
            onVerificationSuccess()
        }
    }
    
    // Show success dialog when verification is successful
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onVerificationSuccess()
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Security,
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
                    "Your device has been successfully verified. You will be redirected to the login screen.", 
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
                    Text("Login")
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Security,
            contentDescription = "Security",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Two-Factor Authentication",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "We've detected a login from a new device.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "We've sent an OTP code to $email.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Please check your email and enter the 6-digit verification code below.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp)
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
                        }
                    },
                    isLastField = i == 5
                )
            }
        }
        
        // Error message
        if (state.isError) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDEDED)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.errorMessage,
                        color = Color(0xFFB00020),
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Verify button - Now verifies the entered OTP
        Button(
            onClick = { 
                keyboardController?.hide()
                focusManager.clearFocus()
                viewModel.verifyOtp(otpValue) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 32.dp),
            enabled = !state.isLoading && otpValue.length == 6
        ) {
            if (state.isLoading) {
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
        
        TextButton(
            onClick = { viewModel.resendVerificationEmail() },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Resend OTP Code",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
        
        TextButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
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