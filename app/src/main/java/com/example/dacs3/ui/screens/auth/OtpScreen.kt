package com.example.dacs3.ui.screens.auth

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.R
import com.example.dacs3.ui.common.CustomTextField
import com.example.dacs3.ui.theme.AppColors
import com.example.dacs3.viewmodel.AuthViewModel
import com.example.dacs3.viewmodel.UiState

@Composable
fun OtpScreen(
    email: String,
    vm: AuthViewModel = hiltViewModel(),
    onVerified: () -> Unit
) {
    val verifyState by vm.verifyState.collectAsState()
    val resendState by vm.resendState.collectAsState()
    var otp by remember { mutableStateOf("") }

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

            OtpLogoPlaceholder()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Xác Thực OTP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnBackgroundColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Vui lòng nhập mã OTP đã được gửi đến $email",
                fontSize = 14.sp,
                color = AppColors.OnSurfaceColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                    OtpTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = "Mã OTP"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Confirm OTP button
            OtpButton(
                text = "Xác Nhận OTP",
                onClick = { vm.verifyOtp(email, otp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            // State handling
            when (verifyState) {
                is UiState.Loading -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(
                        color = AppColors.PrimaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                is UiState.Success -> {
                    LaunchedEffect(Unit) { onVerified() }
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
                            text = (verifyState as UiState.Error).message,
                            color = AppColors.ErrorColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resend OTP button
                    OtpButton(
                        text = "Gửi Lại OTP",
                        onClick = { vm.resendOtp(email) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        isSecondary = true
                    )
                }
                else -> {}
            }

            // Resend success message
            if (resendState is UiState.Success) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.PrimaryColor.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "OTP đã được gửi lại đến $email",
                        color = AppColors.PrimaryColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
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
                    painter = painterResource(id = R.drawable.baseline_key_24),
                    contentDescription = label,
                    tint = AppColors.PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            CustomTextField(
                value = value,
                onValueChange = onValueChange,
                label = label,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OtpLogoPlaceholder() {
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
                strokeWidth = 10f
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x - radius/2, center.y + radius),
                end = Offset(center.x, center.y),
                strokeWidth = 10f
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x, center.y),
                end = Offset(center.x + radius/2, center.y + radius),
                strokeWidth = 10f
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x + radius/2, center.y + radius),
                end = Offset(center.x + radius, center.y - radius),
                strokeWidth = 10f
            )
        }
    }
}

@Composable
fun OtpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false
) {
    val backgroundBrush = if (isSecondary) {
        Brush.horizontalGradient(
            colors = listOf(
                AppColors.SurfaceColor,
                AppColors.SurfaceColor
            )
        )
    } else {
        Brush.horizontalGradient(colors = AppColors.PrimaryGradient)
    }

    val textColor = if (isSecondary) AppColors.PrimaryColor else Color.White
    val borderModifier = if (isSecondary) {
        Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Transparent)
            .padding(2.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.horizontalGradient(colors = AppColors.PrimaryGradient)
            )
            .padding(1.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(AppColors.SurfaceColor)
    } else {
        Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = backgroundBrush
            )
    }

    Box(
        modifier = modifier
            .then(borderModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 14.dp)
        )
    }
}
