package com.example.dacs3.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dacs3.R
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.ui.theme.*
import javax.inject.Inject

@Composable
fun OnboardingScreen(
    navController: NavController,
    sessionManager: SessionManager = hiltViewModel<OnboardingViewModel>().sessionManager
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OnboardingGradientStart,
                        OnboardingGradientEnd
                    )
                )
            )
            .padding(24.dp)
    ) {
        val (image, titleText, descriptionText, startButton) = createRefs()
        
        // Main illustration
        Image(
            painter = painterResource(id = R.drawable.lets_start),
            contentDescription = "TeamNexus Illustration",
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.percent(0.5f)
                },
            contentScale = ContentScale.Fit
        )
        
        // App name
        Text(
            text = "TeamNexus",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TitleTextColor,
            modifier = Modifier
                .constrainAs(titleText) {
                    top.linkTo(image.bottom, margin = 60.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        
        // Description
        Text(
            text = "This powerful platform lets you collaborate, chat in real time, and manage your workspace — all in one place for smarter teamwork!",
            fontSize = 18.sp,
            color = DescriptionTextColor,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier
                .constrainAs(descriptionText) {
                    top.linkTo(titleText.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
        )

        Button(
            onClick = {
                // Mark first time as done when user clicks the button
                sessionManager.setFirstTimeDone()
                
                // Navigate to welcome screen
                navController.navigate("welcome") {
                    popUpTo("onboarding") { inclusive = true }
                }
            },
            modifier = Modifier
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .constrainAs(startButton) {
                    bottom.linkTo(parent.bottom, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = TeamNexusPurple
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Let's Start",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Start Arrow",
                    tint = Color.White
                )
            }
        }
    }
} 