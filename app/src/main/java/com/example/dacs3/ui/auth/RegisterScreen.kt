package com.example.dacs3.ui.auth

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dacs3.ui.auth.otp.navigateToOtpVerification
import com.example.dacs3.util.ValidationUtils
import com.example.dacs3.util.addFocusCleaner
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Biến trạng thái cho việc hiển thị/ẩn mật khẩu
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var contactNumberError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading
    
    // Focus and keyboard management
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Focus management
    val usernameFocusRequester = remember { FocusRequester() }
    var usernameFocused by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var contactNumberFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    var confirmPasswordFocused by remember { mutableStateOf(false) }

    // Request focus to username on first composition
    LaunchedEffect(Unit) {
        awaitFrame()
        usernameFocusRequester.requestFocus()
    }
    
    // Shimmer animation for loading state
    val shimmerColors = listOf(
        Color(0xFFE6E8F0), 
        Color(0xFFF1F3F9), 
        Color(0xFFE6E8F0)
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

    LaunchedEffect(uiState) {
        when {
            uiState.isSuccess && uiState.action == "verify_email" -> {
                // Navigate to OTP verification screen with the registered email and action
                val registeredEmail = uiState.email ?: email
                navController.navigateToOtpVerification(registeredEmail, "verify_email")
            }
            uiState.isSuccess -> {
                // This should not happen in Register flow, but just in case
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            uiState.isError -> {
                showError = true
                // Extract meaningful error message from server response if possible
                val errorMsg = uiState.errorMessage
                errorMessage = when {
                    errorMsg.contains("duplicate key error") && errorMsg.contains("username") -> 
                        "Username already exists. Please choose another username."
                    errorMsg.contains("duplicate key error") && errorMsg.contains("email") -> 
                        "Email already registered. Please use another email."
                    else -> errorMsg
                }
                Log.d("RegisterScreen", "Error message: $errorMessage")
            }
        }
    }

    fun validate(): Boolean {
        Log.d("check email", email)
        var valid = true
        usernameError = ValidationUtils.validateUsername(username).let { if (it is com.example.dacs3.util.ValidationResult.Error) it.message else null }
//        emailError = ValidationUtils.validateEmail(email).let { if (it is com.example.dacs3.util.ValidationResult.Error) it.message else null }
        contactNumberError = ValidationUtils.validatePhone(contactNumber).let { if (it is com.example.dacs3.util.ValidationResult.Error) it.message else null }
//        passwordError = ValidationUtils.validatePassword(password).let { if (it is com.example.dacs3.util.ValidationResult.Error) it.message else null }
        confirmPasswordError = if (confirmPassword != password) "Passwords do not match" else null
        
        if (usernameError != null || emailError != null || contactNumberError != null || passwordError != null || confirmPasswordError != null) 
            valid = false
        return valid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .addFocusCleaner(focusManager, keyboardController),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(340.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            Color(0xFFF5F7FF)
                        )
                    )
                )
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                color = Color(0xFF1A4AC2),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Create an account so you can explore all the\nexisting jobs",
                color = Color(0xFF222222),
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 28.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    if (usernameError != null) usernameError = null
                    if (showError) showError = false
                },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .focusRequester(usernameFocusRequester)
                    .onFocusChanged { usernameFocused = it.isFocused },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (usernameFocused) Color(0xFF1A4AC2) else Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFF5F7FF),
                    focusedContainerColor = Color(0xFFF5F7FF)
                ),
                isError = usernameError != null
            )
            if (usernameError != null) Text(usernameError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                    if (showError) showError = false
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .onFocusChanged { emailFocused = it.isFocused },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (emailFocused) Color(0xFF1A4AC2) else Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFF5F7FF),
                    focusedContainerColor = Color(0xFFF5F7FF)
                ),
                isError = emailError != null
            )
            if (emailError != null) Text(emailError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = contactNumber,
                onValueChange = {
                    contactNumber = it
                    if (contactNumberError != null) contactNumberError = null
                    if (showError) showError = false
                },
                label = { Text("Contact number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .onFocusChanged { contactNumberFocused = it.isFocused },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (contactNumberFocused) Color(0xFF1A4AC2) else Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFF5F7FF),
                    focusedContainerColor = Color(0xFFF5F7FF)
                ),
                isError = contactNumberError != null
            )
            if (contactNumberError != null) Text(contactNumberError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                    if (showError) showError = false
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .onFocusChanged { passwordFocused = it.isFocused },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (passwordFocused) Color(0xFF1A4AC2) else Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFF5F7FF),
                    focusedContainerColor = Color(0xFFF5F7FF)
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF1A4AC2)
                        )
                    }
                },
                isError = passwordError != null
            )
            if (passwordError != null) Text(passwordError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (confirmPasswordError != null) confirmPasswordError = null
                    if (showError) showError = false
                },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .onFocusChanged { confirmPasswordFocused = it.isFocused },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (confirmPasswordFocused) Color(0xFF1A4AC2) else Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFF5F7FF),
                    focusedContainerColor = Color(0xFFF5F7FF)
                ),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF1A4AC2)
                        )
                    }
                },
                isError = confirmPasswordError != null
            )
            if (confirmPasswordError != null) Text(confirmPasswordError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE7E7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFE53935),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Button(
                onClick = {
                    // Hide keyboard when button is clicked
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    
                    if (validate()) {
                        viewModel.register(
                            username = username,
                            email = email,
                            contactNumber = contactNumber,
                            password = password
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4AC2)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Sign Up",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        // Hide keyboard when clicking on sign in link
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        navController.popBackStack()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color(0xFF666666),
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = Color(0xFF1A4AC2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
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
                                color = Color(0xFF1A4AC2),
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
                            text = "Creating account...",
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
} 