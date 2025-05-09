package com.example.dacs3.ui.screens.auth

import android.provider.Settings
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.models.LoginRequest
import com.example.dacs3.models.LoginResponse
import com.example.dacs3.ui.common.AuthButton
import com.example.dacs3.ui.common.AuthTextField
import com.example.dacs3.ui.common.AuthLogo
import com.example.dacs3.ui.common.SpotlightBackground
import com.example.dacs3.ui.theme.AppColors
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.viewmodel.UiState

// Helpers
private fun isEmail(input: String) =
    Patterns.EMAIL_ADDRESS.matcher(input).matches()

private fun isPhone(input: String) =
    input.matches(Regex("^\\d{9,11}\$"))

private fun determineType(input: String) = if (isPhone(input)) "S" else "E"

@Composable
fun LoginScreen(
    vm: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit,
    onNavigateRegister: () -> Unit
) {
    val state by vm.loginState.collectAsState()
    var account by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // Lấy device ID
    val context = LocalContext.current
    val deviceId by remember {
        mutableStateOf(
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        )
    }

    SpotlightBackground(Modifier.fillMaxSize())

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val (logo, title, form, btn, link, st) = createRefs()

        // Logo
        AuthLogo(
            size = 100.dp,
            modifier = Modifier.constrainAs(logo) {
                top.linkTo(parent.top, margin = 80.dp)
                centerHorizontallyTo(parent)
            }
        )

        // Title
        Text(
            "Đăng Nhập",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(logo.bottom, margin = 24.dp)
                centerHorizontallyTo(parent)
            }
        )

        // Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(form) {
                    top.linkTo(title.bottom, margin = 32.dp)
                    start.linkTo(parent.start); end.linkTo(parent.end)
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariantColor)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AuthTextField(
                    value = account,
                    onValueChange = { account = it },
                    label = "Email hoặc SĐT",
                    leadingIcon = Icons.Default.Person
                )
                AuthTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = "Mật khẩu",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )
                error?.let {
                    Text(it, color = AppColors.ErrorColor)
                }
            }
        }

        // Button Đăng nhập
        AuthButton(
            text = "Đăng Nhập",
            onClick = {
                if (account.isBlank() || pass.isBlank()) {
                    error = "Vui lòng nhập đầy đủ thông tin"
                } else {
                    error = null
                    vm.login(
                        LoginRequest(
                            accountName = account,
                            password    = pass,
                            type        = determineType(account),
                            deviceID    = deviceId
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .constrainAs(btn) {
                    top.linkTo(form.bottom, margin = 24.dp)
                    width = Dimension.fillToConstraints
                }
        )

        // Link đến đăng ký
        Row(
            Modifier.constrainAs(link) {
                top.linkTo(btn.bottom, margin = 16.dp)
                centerHorizontallyTo(parent)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Chưa có tài khoản? ")
            Text(
                "Đăng ký",
                color = AppColors.PrimaryColor,
                modifier = Modifier.clickable { onNavigateRegister() }
            )
        }

        // Xử lý state
        when (state) {
            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .constrainAs(st) {
                        top.linkTo(link.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
            )
            is UiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(st) {
                            top.linkTo(link.bottom, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(AppColors.ErrorColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        (state as UiState.Error).message,
                        color = AppColors.ErrorColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            is UiState.Success -> LaunchedEffect((state as UiState.Success<LoginResponse>).data.token) {
                onLoginSuccess((state as UiState.Success<LoginResponse>).data.token)
            }
            else -> Unit
        }
    }
}
