package com.example.dacs3.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.data.model.User
import com.example.dacs3.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    // Set the selected item for bottom navigation
    val selectedItem = 2 // Profile is the third item (index 2)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ người dùng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == 0,
                    onClick = { onNavigateToHome() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
                    label = { Text("Messages") },
                    selected = selectedItem == 1,
                    onClick = { onNavigateToMessages() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == 2,
                    onClick = { /* Already on profile */ }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ProfileContent(
                    user = uiState.user,
                    onUpdateProfile = { username, email -> 
                        viewModel.updateProfile(username, email)
                    },
                    onLogout = {
                        viewModel.logout()
                        onNavigateToLogin()
                    },
                    onError = uiState.error,
                    viewModel = viewModel
                )
            }
            
            // Show success message if profile update was successful
            if (uiState.isUpdateSuccessful) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Cập nhật thông tin thành công!")
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: User?,
    onUpdateProfile: (username: String, email: String) -> Unit,
    onLogout: () -> Unit,
    onError: String?,
    viewModel: ProfileViewModel
) {
    var username by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf(user?.avatar ?: "") }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showAvatarUpdateDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { showAvatarUpdateDialog = true }, // Make clickable to update avatar
            contentAlignment = Alignment.Center
        ) {
            if (user?.avatar != null && user.avatar.isNotEmpty()) {
                // If there's an avatar URL, we would load the image here
                // For demonstration, just show the first letter
                Text(
                    text = user.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Add a camera icon to indicate that avatar can be updated
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Update Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User name
        Text(
            text = user?.name ?: "Chưa có tên",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // User email (empty because not present in model)
        Text(
            text = "No email available",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stats cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(title = "Workspaces", value = "5")
            StatCard(title = "Tasks", value = "12")
            StatCard(title = "Completed", value = "8")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Information Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Thông tin cá nhân",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // User information
                ProfileInfoItem(
                    icon = Icons.Default.Person,
                    label = "Tên người dùng",
                    value = user?.name ?: "Chưa cập nhật"
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = "No email available"
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ProfileInfoItem(
                    icon = Icons.Default.Phone,
                    label = "Số điện thoại",
                    value = "No phone number available"
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ProfileInfoItem(
                    icon = Icons.Default.DateRange,
                    label = "Ngày tham gia",
                    value = user?.created_at?.let { 
                        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it)
                    } ?: "Chưa cập nhật"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showUpdateDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cập nhật thông tin")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cài đặt",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Settings options
                SettingItem(
                    icon = Icons.Default.Notifications,
                    label = "Thông báo",
                    onClick = { /* Handle notification settings */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                SettingItem(
                    icon = Icons.Default.Lock,
                    label = "Bảo mật",
                    onClick = { /* Handle security settings */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                SettingItem(
                    icon = Icons.Default.Info,
                    label = "Về ứng dụng",
                    onClick = { /* Handle about app */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất")
                }
            }
        }
        
        // Error message
        onError?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    // Handle profile update dialog
    if (showUpdateDialog) {
        UpdateProfileDialog(
            initialUsername = username,
            initialEmail = email,
            onDismiss = { showUpdateDialog = false },
            onConfirm = { newUsername, newEmail ->
                onUpdateProfile(newUsername, newEmail)
                username = newUsername
                email = newEmail
                showUpdateDialog = false
            }
        )
    }
    
    // Handle avatar update dialog
    if (showAvatarUpdateDialog) {
        UpdateAvatarDialog(
            initialAvatarUrl = avatarUrl,
            onDismiss = { showAvatarUpdateDialog = false },
            onConfirm = { newAvatarUrl ->
                // In a real implementation, this would call a ViewModel method
                // to update the avatar with the provided URL
                viewModel.updateAvatar(newAvatarUrl)
                avatarUrl = newAvatarUrl
                showAvatarUpdateDialog = false
            }
        )
    }
}
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SettingItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileDialog(
    initialUsername: String,
    initialEmail: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }
    var email by remember { mutableStateOf(initialEmail) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Cập nhật thông tin",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên người dùng") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { onConfirm(username, email) }) {
                        Text("Xác nhận")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAvatarDialog(
    initialAvatarUrl: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var avatarUrl by remember { mutableStateOf(initialAvatarUrl ?: "") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Cập nhật ảnh đại diện",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("URL ảnh") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { onConfirm(avatarUrl) }) {
                        Text("Xác nhận")
                    }
                }
            }
        }
    }
}