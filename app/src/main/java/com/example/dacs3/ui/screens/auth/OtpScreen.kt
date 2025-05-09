package com.example.dacs3.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.common.*
import com.example.dacs3.ui.theme.AppColors
import com.example.dacs3.viewmodel.OtpViewModel
import com.example.dacs3.viewmodel.UiState

@Composable
fun OtpScreen(
    email: String,
    vm: OtpViewModel = hiltViewModel(),
    onVerified: () -> Unit
) {
    val verifyState by vm.verifyState.collectAsState()
    val resendState by vm.resendState.collectAsState()
    var otp by remember { mutableStateOf("") }

    SpotlightBackground(Modifier.fillMaxSize())

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val (logo, title, form, btn, st, resendBtn, success) = createRefs()

        AuthLogo(
            modifier = Modifier.constrainAs(logo) {
                top.linkTo(parent.top, margin = 80.dp)
                centerHorizontallyTo(parent)
            }
        )

        Text(
            "Xác Thực OTP",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(logo.bottom, margin = 24.dp)
                centerHorizontallyTo(parent)
            }
        )

        AuthTextField(
            value = otp,
            onValueChange = { otp = it },
            label = "Mã OTP",
            leadingIcon = Icons.Default.Phone,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(form) {
                    top.linkTo(title.bottom, margin = 32.dp)
                    width = Dimension.fillToConstraints
                }
        )

        AuthButton(
            "Xác Nhận OTP",
            onClick = { vm.verifyOtp(email, otp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .constrainAs(btn) {
                    top.linkTo(form.bottom, margin = 24.dp)
                    width = Dimension.fillToConstraints
                }
        )

        when (verifyState) {
            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .constrainAs(st) {
                        top.linkTo(btn.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
            )
            is UiState.Success -> LaunchedEffect(Unit) { onVerified() }
            is UiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(st) {
                            top.linkTo(btn.bottom, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(AppColors.ErrorColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        (verifyState as UiState.Error).message,
                        color = AppColors.ErrorColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                AuthButton(
                    "Gửi Lại OTP",
                    onClick = { vm.resendOtp(email) },
                    isSecondary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .constrainAs(resendBtn) {
                            top.linkTo(st.bottom, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                )
            }
            else -> Unit
        }

        if (resendState is UiState.Success) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(success) {
                        top.linkTo(btn.bottom, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(AppColors.PrimaryColor.copy(alpha = 0.2f))
            ) {
                Text(
                    "OTP đã gửi lại đến $email",
                    color = AppColors.PrimaryColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
