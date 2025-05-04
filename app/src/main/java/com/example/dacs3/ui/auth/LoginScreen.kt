package com.example.dacs3.ui.auth

import android.util.Patterns
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.network.LoginRequest
import com.example.dacs3.network.LoginResponse
import com.example.dacs3.ui.common.AppButton
import com.example.dacs3.ui.common.CustomTextField
import com.example.dacs3.ui.theme.AppColors
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.viewmodel.UiState

private fun isEmail(input: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(input).matches()
}

private fun isPhoneNumber(input: String): Boolean {
    return input.matches(Regex("^\\d{9,11}$"))
}

private fun determineInputType(input: String): String {
    return when {
        isEmail(input) -> "E"
        isPhoneNumber(input) -> "S"
        else -> "E"
    }
}

@Composable
fun LoginScreen(
    vm: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit,
    onNavigateRegister: () -> Unit
) {
    val state by vm.loginState.collectAsState()
    var accountName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundGradient = listOf(
        AppColors.BackgroundColor,
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
                        AppColors.PrimaryColor.copy(alpha = 0.2f),
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
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            LoginLogoPlaceholder()

            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "Đăng Nhập",
                fontSize = 24.sp, // Tăng kích thước chữ
                fontWeight = FontWeight.Bold,
                color = AppColors.OnBackgroundColor
            )

            Spacer(modifier = Modifier.height(8.dp)) // Giảm khoảng cách giữa tiêu đề và phụ đề

            Text(
                text = "Nhập thông tin để đăng nhập vào tài khoản",
                fontSize = 14.sp, // Tăng kích thước chữ
                color = AppColors.OnSurfaceColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp)) // Tăng khoảng cách giữa phụ đề và form

            // Form card
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.SurfaceVariantColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LoginTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = "Email hoặc Số điện thoại",
                        icon = Icons.Default.Person
                    )

                    LoginTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = AppColors.ErrorColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Tăng khoảng cách giữa form và nút đăng nhập

            // Login button
            LoginButton(
                text = "Đăng Nhập",
                onClick = {
                    if (accountName.isBlank() || password.isBlank()) {
                        errorMessage = "Vui lòng nhập đầy đủ thông tin"
                    } else {
                        errorMessage = null
                        // Tự động xác định loại đầu vào (email hoặc số điện thoại)
                        val inputType = determineInputType(accountName)
                        vm.login(LoginRequest(accountName, password, inputType))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp) // Tăng chiều cao nút
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Register link
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Chưa có tài khoản? ",
                    fontSize = 14.sp, // Tăng kích thước chữ
                    color = AppColors.OnSurfaceColor.copy(alpha = 0.9f)
                )
                Text(
                    text = "Đăng ký",
                    fontSize = 14.sp, // Tăng kích thước chữ
                    color = AppColors.PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateRegister() }
                )
            }

            // Loading state
            when (state) {
                is UiState.Loading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = AppColors.PrimaryColor,
                        modifier = Modifier.size(32.dp) // Tăng kích thước
                    )
                }
                is UiState.Success -> {
                    val token = (state as UiState.Success<LoginResponse>).data.token
                    LaunchedEffect(token) {
                        onLoginSuccess(token)
                    }
                }
                is UiState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.ErrorColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = (state as UiState.Error).message,
                            color = AppColors.ErrorColor,
                            fontSize = 14.sp, // Tăng kích thước chữ
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp) // Tăng padding
                        )
                    }
                }
                else -> {}
            }

            // Thêm khoảng trống ở cuối để đảm bảo có thể cuộn lên khi bàn phím hiện
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = AppColors.SurfaceColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppColors.PrimaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = AppColors.PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Sử dụng CustomTextField với một số điều chỉnh
            CustomTextField(
                value = value,
                onValueChange = onValueChange,
                label = if (label == "Email hoặc Số điện thoại") "Email/SĐT" else label, // Rút gọn label
                isPassword = isPassword,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LoginLogoPlaceholder() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = AppColors.PrimaryGradient
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Tạo logo mẫu bằng Canvas
        Canvas(modifier = Modifier.size(60.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 3

            drawLine(
                color = Color.White,
                start = Offset(center.x - radius, center.y - radius),
                end = Offset(center.x - radius/2, center.y + radius),
                strokeWidth = 10f // Tăng độ dày
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x - radius/2, center.y + radius),
                end = Offset(center.x, center.y),
                strokeWidth = 10f // Tăng độ dày
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x, center.y),
                end = Offset(center.x + radius/2, center.y + radius),
                strokeWidth = 10f // Tăng độ dày
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x + radius/2, center.y + radius),
                end = Offset(center.x + radius, center.y - radius),
                strokeWidth = 10f // Tăng độ dày
            )
        }
    }
}

@Composable
fun LoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(colors = AppColors.PrimaryGradient)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 14.dp)
        )
    }
}
