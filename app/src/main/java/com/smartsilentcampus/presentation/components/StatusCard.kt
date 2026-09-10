package com.smartsilentcampus.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsilentcampus.domain.model.SupportedRingerMode
import com.smartsilentcampus.presentation.theme.*

@Composable
fun StatusCard(
    ringerMode: SupportedRingerMode,
    isAutomationEnabled: Boolean,
    isPaused: Boolean,
    pausedMinutes: Int,
    activeZoneName: String?,
    onEmergencyRestore: () -> Unit,
    onResumeAutomation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = when {
        isPaused -> AmberOrange
        !isAutomationEnabled -> Color.Gray
        ringerMode == SupportedRingerMode.SILENT || ringerMode == SupportedRingerMode.DO_NOT_DISTURB -> DeepBlue40
        ringerMode == SupportedRingerMode.VIBRATE -> Purple40
        else -> EmeraldGreen40
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (ringerMode) {
                            SupportedRingerMode.SILENT -> Icons.Default.NotificationsOff
                            SupportedRingerMode.VIBRATE -> Icons.Default.Vibration
                            SupportedRingerMode.DO_NOT_DISTURB -> Icons.Default.DoNotDisturb
                            SupportedRingerMode.NORMAL -> Icons.Default.NotificationsActive
                        },
                        contentDescription = "Sound Status",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when {
                            isPaused -> "AUTOMATION PAUSED ($pausedMinutes m left)"
                            !isAutomationEnabled -> "AUTOMATION DISABLED"
                            else -> "${ringerMode.displayName.uppercase()} MODE"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when {
                    isPaused -> "Automation is paused temporarily. Normal sound settings are active."
                    !isAutomationEnabled -> "Silent mode will not be triggered automatically until re-enabled."
                    activeZoneName != null -> "Activated because you entered $activeZoneName."
                    else -> "You are currently outside all configured silent zones."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isPaused) {
                    Button(
                        onClick = onResumeAutomation,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Resume Now", fontWeight = FontWeight.SemiBold)
                    }
                } else if (activeZoneName != null) {
                    Button(
                        onClick = onEmergencyRestore,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = CoralRed
                        )
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Emergency Restore", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
