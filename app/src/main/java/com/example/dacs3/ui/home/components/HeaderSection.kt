package com.example.dacs3.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.dacs3.ui.theme.DACS3Theme

@Composable
fun HeaderSection(
    username: String,
    modifier: Modifier = Modifier,
    onWorkspaceClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            val (greeting, workspace, notification) = createRefs()

            // Greeting with user icon using ConstraintLayout inside
            ConstraintLayout(
                modifier = Modifier.constrainAs(greeting) {
                    top.linkTo(parent.top, margin = 50.dp)
                    start.linkTo(parent.start)
                }
            ) {
                val (iconRef, textRef) = createRefs()

                // Icon
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color(0xFF4A63B9),
                    modifier = Modifier.constrainAs(iconRef) {
                        start.linkTo(parent.start)
                        centerVerticallyTo(parent)
                    }
                        .width(40.dp)
                        .height(40.dp)
                )

                // Text
                Text(
                    text = "Hello $username",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = Color(0xFF4A63B9),
                    modifier = Modifier.constrainAs(textRef) {
                        start.linkTo(iconRef.end, margin = 8.dp)
                        centerVerticallyTo(parent)
                    }
                )

                Text(
                    text = "Your Workspace",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4A63B9)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle workspace dropdown",
                    tint = Color(0xFF4A63B9)
                )

                // Notification icon
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF4A63B9),
                    modifier = Modifier
                        .constrainAs(notification) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                        .clickable { onNotificationClick() }
                )
            }

    }}
}
