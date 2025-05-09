package com.example.dacs3.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs3.ui.theme.AppColors

// 1. Spotlight background (radial + gradient)
@Composable
fun SpotlightBackground(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(AppColors.BackgroundColor, Color(0xFF121212)),
    spotlightCenter: Offset = Offset(0.8f, 0.2f),
    spotlightRadiusFraction: Float = 0.5f,
) {
    Box(modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // vertical gradient
            drawRect(
                brush = Brush.verticalGradient(gradientColors)
            )
            // radial spotlight
            val r = size.width * spotlightRadiusFraction
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AppColors.PrimaryColor.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(size.width * spotlightCenter.x, size.height * spotlightCenter.y),
                    radius = r
                ),
                center = Offset(size.width * spotlightCenter.x, size.height * spotlightCenter.y),
                radius = r
            )
        }
    }
}

// 2. Reusable Logo placeholder
@Composable
fun AuthLogo(
    size: Dp = 80.dp,
    canvasSize: Dp = 50.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(AppColors.PrimaryGradient)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(canvasSize)) {
            val w = size.toPx()
            val center = Offset(w/2, w/2)
            val r = w/3
            drawLine(Color.White, center - Offset(r, r), center + Offset(r, r/3), strokeWidth = 8f)
            drawLine(Color.White, center + Offset(r, r/3), center - Offset(r/3, r), strokeWidth = 8f)
            drawLine(Color.White, center - Offset(r/3, r), center + Offset(r/3, -r), strokeWidth = 8f)
            drawLine(Color.White, center + Offset(r/3, -r), center - Offset(r, r/3), strokeWidth = 8f)
        }
    }
}

// 3. Reusable text field surface
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = AppColors.SurfaceColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                leadingIcon,
                contentDescription = label,
                tint = AppColors.PrimaryColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
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

// 4. Reusable primary button
@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false
) {
    val brush = if (isSecondary)
        Brush.horizontalGradient(listOf(AppColors.SurfaceColor, AppColors.SurfaceColor))
    else
        Brush.horizontalGradient(AppColors.PrimaryGradient)

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(brush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSecondary) AppColors.PrimaryColor else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
