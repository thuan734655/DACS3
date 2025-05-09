package com.example.dacs3.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.models.RegisterRequest
import com.example.dacs3.models.RegisterResponse
import com.example.dacs3.ui.common.*
import com.example.dacs3.ui.theme.AppColors
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.viewmodel.UiState

private fun validate(username: String, email: String, phone: String, pass: String, conf: String): String? {
    if (username.length < 3) return "Tên phải ≥3 ký tự"
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Email không hợp lệ"
    if (!phone.matches(Regex("^\\d{10}\$"))) return "SĐT 10 chữ số"
    if (pass.length < 6) return "Mật khẩu ≥6 ký tự"
    if (pass != conf) return "Mật khẩu không khớp"
    return null
}

@Composable
fun RegisterScreen(
    vm: AuthViewModel = hiltViewModel(),
    onNavigateOtp: (String) -> Unit,
    onNavigateLogin: () -> Unit
) {
    val state by vm.registerState.collectAsState()
    var user by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var conf by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    SpotlightBackground(Modifier.fillMaxSize())

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val (logo, title, form, btn, link, st) = createRefs()

        AuthLogo(
            modifier = Modifier.constrainAs(logo) {
                top.linkTo(parent.top, margin = 80.dp)
                centerHorizontallyTo(parent)
            }
        )

        Text(
            "Đăng Ký",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(logo.bottom, margin = 24.dp)
                centerHorizontallyTo(parent)
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(form) {
                    top.linkTo(title.bottom, margin = 32.dp)
                    width = Dimension.fillToConstraints
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariantColor)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(16.dp)) {
                AuthTextField(user, { user = it }, "Username", Icons.Default.Person)
                AuthTextField(email, { email = it }, "Email", Icons.Default.Email)
                AuthTextField(phone, { phone = it }, "SĐT", Icons.Default.Phone)
                AuthTextField(pass, { pass = it }, "Mật khẩu", Icons.Default.Lock, isPassword = true)
                AuthTextField(conf, { conf = it }, "Nhập lại mật khẩu", Icons.Default.Lock, isPassword = true)
                error?.let { Text(it, color = AppColors.ErrorColor) }
            }
        }

        AuthButton(
            "Đăng Ký",
            onClick = {
                validate(user, email, phone, pass, conf)?.let { error = it }
                    ?: run {
                        error = null
                        vm.register(RegisterRequest(user, email, phone, pass))
                    }
            },
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .constrainAs(btn) {
                    top.linkTo(form.bottom, margin = 24.dp)
                    width = Dimension.fillToConstraints
                }
        )

        Row(
            Modifier.constrainAs(link) {
                top.linkTo(btn.bottom, margin = 16.dp)
                centerHorizontallyTo(parent)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đã có tài khoản? ")
            Text("Đăng nhập", color = AppColors.PrimaryColor, modifier = Modifier.clickable { onNavigateLogin() })
        }

        when (state) {
            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .constrainAs(st) {
                        top.linkTo(link.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
            )
            is UiState.Success -> LaunchedEffect((state as UiState.Success<RegisterResponse>).data) {
                onNavigateOtp(email)
            }
            is UiState.Error -> Card(
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
            else -> Unit
        }
    }
}
