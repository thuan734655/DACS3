package com.example.dacs3.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.ui.components.BottomNavigationBar
import com.example.dacs3.ui.epic.EpicViewModel

@Composable
fun DashboardScreen(
    onBoardClick: () -> Unit,
    onSprintClick: () -> Unit,
    onEpicClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onMessageClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        val (title, content, bottomNav) = createRefs()
        val epicViewModel: EpicViewModel = hiltViewModel()
        val epicUiState by epicViewModel.uiState.collectAsState()
        val selectedEpicId = epicUiState.selectedEpic?._id

        // Tiêu đề
        Text(
            text = "Dashboards",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 40.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
        )

        // Nội dung
        Column(
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(title.bottom, margin = 24.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        ) {
            DashboardItem(text = "Go to Board", onClick = onBoardClick)
            Spacer(modifier = Modifier.height(16.dp))
            DashboardItem(text = "Go to sprint", onClick = onSprintClick)
            Spacer(modifier = Modifier.height(16.dp))
            DashboardItem(text = "Go to epic", onClick = onEpicClick)
            Spacer(modifier = Modifier.height(16.dp))
            DashboardItem(text = "Go to task",
                onClick = {
                selectedEpicId?.let { onTaskClick(it) }
                }
            )
        }

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = "dashboard",
            onHomeClick = onHomeClick,
            onMessageClick = onMessageClick,
            onDashboardClick = onDashboardClick,
            onProfileClick = onProfileClick,
            modifier = Modifier
                .constrainAs(bottomNav) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardItem(
    text: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = Color(0xFF6200EE),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}