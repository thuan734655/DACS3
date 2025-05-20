package com.example.dacs3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskPriorityBadge(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (priority) {
        "LOW" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Light green / Dark green
        "MEDIUM" -> Color(0xFFFFF3E0) to Color(0xFFEF6C00) // Light orange / Dark orange
        "HIGH" -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Light red / Dark red
        else -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Default to low priority colors
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = when (priority) {
                "LOW" -> "Low"
                "MEDIUM" -> "Medium" 
                "HIGH" -> "High"
                else -> priority
            },
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}