package com.example.dacs3.ui.auth

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs3.network.RegisterRequest
import com.example.dacs3.ui.common.CustomTextField
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.viewmodel.UiState
import android.util.Patterns
import com.example.dacs3.ui.theme.AppColors.BackgroundColor
import com.example.dacs3.ui.theme.AppColors.ErrorColor
import com.example.dacs3.ui.theme.AppColors.OnBackgroundColor
import com.example.dacs3.ui.theme.AppColors.OnSurfaceColor
import com.example.dacs3.ui.theme.AppColors.PrimaryColor
import com.example.dacs3.ui.theme.AppColors.PrimaryGradient
import com.example.dacs3.ui.theme.AppColors.SurfaceColor
import com.example.dacs3.ui.theme.AppColors.SurfaceVariantColor

fun validateRegisterInput(
    username: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String
): String? {
    if (username.isBlank() || username.length < 3) return "Tên người dùng phải có ít nhất 3 ký tự"
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Email không hợp lệ"
    if (!phone.matches(Regex("^\\d{9,11}$"))) return "Số điện thoại phải từ 9–11 chữ số"
    if (password.length < 6) return "Mật khẩu phải có ít nhất 6 ký tự"
    if (password != confirmPassword) return "Mật khẩu xác nhận không khớp"
    return null
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = SurfaceColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = PrimaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            CustomTextField(
                value = value,
                onValueChange = onValueChange,
                label = label,
                isPassword = isPassword,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LogoPlaceholder() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = PrimaryGradient
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Tạo logo bằng Canvas
        Canvas(modifier = Modifier.size(50.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 3

            // Vẽ chữ W
            drawLine(
                color = Color.White,
                start = Offset(center.x - radius, center.y - radius),
                end = Offset(center.x - radius/2, center.y + radius),
                strokeWidth = 8f
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x - radius/2, center.y + radius),
                end = Offset(center.x, center.y),
                strokeWidth = 8f
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x, center.y),
                end = Offset(center.x + radius/2, center.y + radius),
                strokeWidth = 8f
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x + radius/2, center.y + radius),
                end = Offset(center.x + radius, center.y - radius),
                strokeWidth = 8f
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(colors = PrimaryGradient)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
fun RegisterScreen(
    vm: AuthViewModel = hiltViewModel(),
    onNavigateOtp: (String) -> Unit,
    onNavigateLogin: () -> Unit
) {
    val state by vm.registerState.collectAsState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundGradient = listOf(
        BackgroundColor,
        Color(0xFF121212)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = backgroundGradient)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.2f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = size.width * 0.5f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            LogoPlaceholder()

            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Text(
                text = "Đăng Ký Tài Khoản",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackgroundColor
            )

            Text(
                text = "Nhập thông tin để tạo tài khoản mới",
                fontSize = 12.sp,
                color = OnSurfaceColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Form card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceVariantColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ModernTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Tên người dùng",
                        icon = Icons.Default.Person
                    )

                    ModernTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        icon = Icons.Default.Email
                    )

                    ModernTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Số điện thoại",
                        icon = Icons.Default.Phone
                    )

                    ModernTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    ModernTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = "Nhập lại mật khẩu",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = ErrorColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register button
            GradientButton(
                text = "Đăng Ký",
                onClick = {
                    val error = validateRegisterInput(username, email, phone, password, confirm)
                    if (error != null) {
                        errorMessage = error
                    } else {
                        errorMessage = null
                        vm.register(RegisterRequest(username, email, phone, password))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Login link
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Đã có tài khoản? ",
                    fontSize = 12.sp,
                    color = OnSurfaceColor.copy(alpha = 0.9f)
                )
                Text(
                    text = "Đăng nhập",
                    fontSize = 12.sp,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateLogin() }
                )
            }

            // Loading state
            when (state) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                is UiState.Success -> {
                    LaunchedEffect(Unit) {
                        onNavigateOtp(email.trim())
                    }
                }
                is UiState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = (state as UiState.Error).message,
                            color = ErrorColor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        )
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
