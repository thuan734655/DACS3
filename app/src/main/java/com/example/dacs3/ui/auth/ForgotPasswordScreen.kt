package com.example.dacs3.ui.auth

import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
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
import androidx.navigation.NavController
import com.example.dacs3.ui.auth.otp.navigateToOtpVerification
import com.example.dacs3.util.addFocusCleaner
import com.example.dacs3.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    
    // Handle UI state changes
    LaunchedEffect(uiState) {
        when {
            uiState.isSuccess && uiState.action == "verify_otp_for_reset" -> {
                // Navigate to OTP verification screen for password reset
                delay(300) // Short delay for better UX
                uiState.email?.let { email ->
                    navController.navigateToOtpVerification(email, "reset_password")
                }
            }
            uiState.isError -> {
                showError = true
                errorMessage = uiState.errorMessage
            }
        }
    }
    
    // Request focus on email field when screen loads
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    // Shimmer animation for loading state
    val shimmerColors = listOf(
        GradientStart,
        GradientMiddle,
        GradientEnd
    )
    
    var shimmerTranslation by remember { mutableStateOf(0f) }
    LaunchedEffect(isLoading) {
        if (isLoading) {
            while (true) {
                shimmerTranslation = if (shimmerTranslation < 900f) shimmerTranslation + 100f else 0f
                delay(100)
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGrey)
            .addFocusCleaner(focusManager, keyboardController),
        contentAlignment = Alignment.Center
    ) {
        // Back button at the top left
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TeamNexusPurple
            )
        }
        
        Column(
            modifier = Modifier
                .width(340.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            InputBackground
                        )
                    )
                )
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TeamNexusPurple,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "Enter the email address associated with your account",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MediumGrey,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Email input field
            TextField(
                value = email,
                onValueChange = { email = it; showError = false },
                placeholder = { Text("Email address") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = TeamNexusPurple
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = CardLightGrey,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (email.isNotBlank()) {
                            viewModel.forgotPassword(email)
                        }
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error message
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = ErrorRed,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Send reset link button
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (email.isNotBlank()) {
                        viewModel.forgotPassword(email)
                    }
                },
                enabled = !isLoading && email.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TeamNexusPurple,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(top = 12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Send Reset Code")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Back to login link
            Text(
                text = "Back to Login",
                color = TeamNexusPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(vertical = 8.dp)
            )
        }
        
        // Loading overlay with shimmer effect when isLoading is true
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(enabled = false) { /* Prevent clicks through overlay */ },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box {
                            CircularProgressIndicator(
                                color = TeamNexusPurple,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            // Shimmer overlay
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = shimmerColors,
                                            start = androidx.compose.ui.geometry.Offset(
                                                shimmerTranslation - 900f, 
                                                shimmerTranslation - 900f
                                            ),
                                            end = androidx.compose.ui.geometry.Offset(
                                                shimmerTranslation, 
                                                shimmerTranslation
                                            )
                                        ),
                                        alpha = 0.3f,
                                        shape = CircleShape
                                    )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sending reset code...",
                            color = DarkGrey,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
} 