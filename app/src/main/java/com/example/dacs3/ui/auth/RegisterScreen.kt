package com.example.dacs3.ui.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dacs3.util.ValidationUtils
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

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

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var contactNumberError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading

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

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.RegisterSuccess -> {
                // Show success message and navigate to login
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthState.Success -> {
                // This should not happen in Register flow, but just in case
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                showError = true
                errorMessage = (authState as AuthState.Error).message
            }
            else -> {}
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
            .background(Color(0xFFF7F7F7)),
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
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
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
                isError = passwordError != null
            )
            if (passwordError != null) Text(passwordError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (confirmPasswordError != null) confirmPasswordError = null
                },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
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
                isError = confirmPasswordError != null
            )
            if (confirmPasswordError != null) Text(confirmPasswordError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)) else Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (validate()) {
                        viewModel.register(username, email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(10.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4AC2)),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp) else Text("Sign up", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            }
            AnimatedVisibility(visible = showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Already have an account",
                color = Color(0xFF222222),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
} 