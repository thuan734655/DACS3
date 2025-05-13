package com.example.dacs3.ui.workspaces.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dacs3.ui.theme.TeamNexusPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkspaceScreen(
    navController: NavController,
    viewModel: CreateWorkspaceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // Trigger animations
    LaunchedEffect(key1 = true) {
        delay(100)
        showContent = true
    }
    
    LaunchedEffect(key1 = state.isCreated) {
        if (state.isCreated) {
            showSuccessMessage = true
            delay(1500)
            navController.popBackStack()
        }
    }
    
    LaunchedEffect(key1 = state.error) {
        state.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(message = it)
                viewModel.clearError()
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create Workspace") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            if (showSuccessMessage) {
                SuccessAnimation()
            } else {
                WorkspaceForm(
                    state = state,
                    isVisible = showContent,
                    onNameChange = viewModel::updateName,
                    onDescriptionChange = viewModel::updateDescription,
                    onCreateWorkspace = viewModel::createWorkspace
                )
            }
            
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TeamNexusPurple)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkspaceForm(
    state: CreateWorkspaceState,
    isVisible: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCreateWorkspace: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val cardElevation by animateDpAsState(
        targetValue = if (isVisible) 4.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardElevation"
    )
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { it / 2 }
                ),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Workspace Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    // Name field
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Workspace Name") },
                        placeholder = { Text("e.g. Team Nexus") },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = TeamNexusPurple,
                            focusedLabelColor = TeamNexusPurple,
                            cursorColor = TeamNexusPurple
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    
                    // Description field
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("Briefly describe your workspace") },
                        minLines = 3,
                        maxLines = 5,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = TeamNexusPurple,
                            focusedLabelColor = TeamNexusPurple,
                            cursorColor = TeamNexusPurple
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Create Button
            Button(
                onClick = onCreateWorkspace,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = state.name.isNotBlank() && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TeamNexusPurple,
                    disabledContainerColor = TeamNexusPurple.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "CREATE WORKSPACE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WorkspaceIconPreview(name: String) {
    val displayLetter = if (name.isNotBlank()) name.first().uppercase() else "W"
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Workspace visual with animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(TeamNexusPurple),
            contentAlignment = Alignment.Center
        ) {
            // Main circle with letter
            Text(
                text = displayLetter,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Decorative elements around the circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val center = Offset(canvasWidth / 2, canvasHeight / 2)
                val radius = (canvasWidth.coerceAtMost(canvasHeight) / 2) * 0.85f
                
                // Draw decorative dots
                for (i in 0 until 8) {
                    val angle = (i * 45f) * (Math.PI / 180f).toFloat()
                    val x = center.x + radius * cos(angle)
                    val y = center.y + radius * sin(angle)
                    
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
                
                // Draw connecting lines
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                )
            }
        }
        
        // Illustration of people collaborating
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy((-12).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Person 1
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(TeamNexusPurple.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Person 2
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6D4C96)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Person 3
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4E2D80)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Additional people indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3D236A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SuccessAnimation() {
    var showMessage by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = true) {
        delay(300)
        showMessage = true
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showMessage,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { it / 2 }
                    )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Workspace Created!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TeamNexusPurple
                )
                
                Text(
                    text = "Redirecting...",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
} 