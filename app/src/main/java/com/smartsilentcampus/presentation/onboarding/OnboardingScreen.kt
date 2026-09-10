package com.smartsilentcampus.presentation.onboarding

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartsilentcampus.permissions.PermissionManager
import com.smartsilentcampus.presentation.theme.DeepBlue40

@Composable
fun OnboardingScreen(
    permissionManager: PermissionManager,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentStep) {
                0 -> {
                    Icon(
                        Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = DeepBlue40
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Smart Silent Campus",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Automatically keep your phone silent in classrooms, colleges, libraries and important campus zones.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { currentStep = 1 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get Started")
                    }
                }
                1 -> {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = DeepBlue40
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Location & Geofencing",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "SmartSilent uses Android geofencing to detect when you cross into a silent zone. We never continuously upload or track your live location.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = {
                            context.startActivity(permissionManager.openAppSettings())
                            currentStep = 2
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Location Permission")
                    }
                    TextButton(onClick = { currentStep = 2 }) {
                        Text("Continue")
                    }
                }
                2 -> {
                    Icon(
                        Icons.Default.VolumeOff,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = DeepBlue40
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Do Not Disturb Access",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Android requires Notification Policy Access to switch sound profiles between Silent, Vibrate, and Normal automatically.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = {
                            context.startActivity(permissionManager.openNotificationPolicySettings())
                            onFinish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Sound Settings")
                    }
                    TextButton(onClick = onFinish) {
                        Text("Finish Setup")
                    }
                }
            }
        }
    }
}
