package com.example.dacs3.ui.auth

import android.provider.Settings
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dacs3.ui.auth.otp.navigateToOtpVerification
import com.example.dacs3.ui.theme.*
import com.example.dacs3.util.addFocusCleaner
import com.example.dacs3.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Focus requesters for better keyboard management
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    
    // Focus on email field initially
    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    LaunchedEffect(uiState) {
        when {
            uiState.isSuccess -> {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
            uiState.action == "verify_email" -> {
                // Yêu cầu server gửi OTP rồi mới chuyển đến màn hình xác thực
                Log.d("LoginScreen", "Email verification required for: ${uiState.email}")
                uiState.email?.let { emailAddress ->
                    // Gửi yêu cầu OTP đến server
                    viewModel.requestVerificationOtp(emailAddress)
                    
                    // Sau đó chuyển đến màn hình OTP
                    navController.navigateToOtpVerification(emailAddress, "verify_email", "login")
                }
            }
            uiState.action == "2fa" -> {
                // Navigate to OTP verification when 2FA is required
                Log.d("LoginScreen", "2FA required, navigating to OTP screen with email: ${uiState.email}")
                uiState.email?.let { email ->
                    navController.navigateToOtpVerification(email, "2fa", "login")
                }
            }
            uiState.isError -> {
                showError = true
                // Extract meaningful error message from server response if possible
                val errorMsg = uiState.errorMessage
                errorMessage = when {
                    errorMsg.contains("Invalid credentials") -> 
                        "Invalid email or password. Please try again."
                    errorMsg.contains("User not found") -> 
                        "Account not found. Please check your email or create an account."
                    else -> errorMsg
                }
                Log.d("LoginScreen", "Error message: $errorMessage")
            }
        }
    }

    // Shimmer animation for loading state
    val shimmerColors = listOf(
        GradientStart,
        GradientMiddle,
        GradientEnd
    )
    
    val shimmerBrush = remember {
        Brush.linearGradient(
            colors = shimmerColors,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(900f, 900f)
        )
    }
    
    // Update shimmer animation
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
        Column(
            modifier = Modifier
                .width(320.dp)
                .shadow(8.dp, RoundedCornerShape(28.dp))
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Welcome back you've\nbeen missed!",
                color = DarkGrey,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it 
                    if (showError) showError = false
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(emailFocusRequester),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TeamNexusPurple ,
                    unfocusedBorderColor = InputBorderDefault,
                    unfocusedContainerColor = InputBackground,
                    focusedContainerColor = InputBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = TeamNexusPurple
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            
            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it 
                    if (showError) showError = false
                },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .focusRequester(passwordFocusRequester),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =  TeamNexusPurple ,
                    unfocusedBorderColor = InputBorderDefault,
                    unfocusedContainerColor = InputBackground,
                    focusedContainerColor = InputBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = TeamNexusPurple
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = TeamNexusPurple
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Forgot password text
                Text(
                    text = "Forgot password?",
                    color = TeamNexusPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { 
                        // Hide keyboard when clicking on forgot password
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        navController.navigate("forgot_password") 
                    }
                )
            }
            
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (errorMessage.contains("verify your email", ignoreCase = true)) 
                            ErrorBackground else ErrorBackground // Amber 100 for verify email (warning)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = if (errorMessage.contains("verify your email", ignoreCase = true))
                                ErrorRed else ErrorRed, // Amber for verify, Red for error
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (errorMessage.contains("verify your email", ignoreCase = true)) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    // Gửi OTP và chuyển sang màn hình xác thực
                                    viewModel.requestVerificationOtp(email)
                                    navController.navigateToOtpVerification(email, "verify_email", "login")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TeamNexusPurple
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Verify Now", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Dismiss keyboard when login button is clicked
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        
                        // Determine if input is email or phone
                        val isEmail = email.contains("@")
                        // Get a device ID directly from settings
                        val deviceId = Settings.Secure.getString(
                            navController.context.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                        // Log the device ID for debugging
                        Log.d("LoginScreen", "Using direct Android ID for login: $deviceId")
                        
                        // Call login without remember me parameter
                        viewModel.login(email, password, isEmail, deviceId)
                    } else {
                        showError = true
                        errorMessage = "Please enter email and password"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(6.dp, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TeamNexusPurple),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Sign in", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
        
        // Loading overlay with shimmer effect
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
                            text = "Signing in...",
                            color = DarkGrey,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                )
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .clickable {
                    // Hide keyboard when clicking on register
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    navController.navigate("register")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create new account",
                color = TextDark,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }
    }
} 