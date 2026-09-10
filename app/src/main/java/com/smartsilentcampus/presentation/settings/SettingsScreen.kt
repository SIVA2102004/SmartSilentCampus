package com.smartsilentcampus.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsilentcampus.presentation.theme.EmeraldGreen40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Automation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Geofence Automation", fontWeight = FontWeight.SemiBold)
                            Text("Automatically trigger sound profiles when entering campus zones", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = uiState.isAutomationEnabled,
                            onCheckedChange = { viewModel.toggleAutomation(it) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Entry Notifications", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = uiState.isEntryNotificationEnabled,
                            onCheckedChange = { viewModel.toggleEntryNotification(it) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Exit Restoration Notifications", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = uiState.isExitNotificationEnabled,
                            onCheckedChange = { viewModel.toggleExitNotification(it) }
                        )
                    }
                }
            }

            Text("System Permissions & Access", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PermissionRow(
                        name = "Fine Location",
                        granted = uiState.hasFineLocation,
                        onClickFix = { context.startActivity(viewModel.permissionManager.openAppSettings()) }
                    )
                    PermissionRow(
                        name = "Background Location",
                        granted = uiState.hasBackgroundLocation,
                        onClickFix = { context.startActivity(viewModel.permissionManager.openAppSettings()) }
                    )
                    PermissionRow(
                        name = "Notifications",
                        granted = uiState.hasNotificationPermission,
                        onClickFix = { context.startActivity(viewModel.permissionManager.openAppSettings()) }
                    )
                    PermissionRow(
                        name = "Do Not Disturb (DND) Access",
                        granted = uiState.hasDndAccess,
                        onClickFix = { context.startActivity(viewModel.permissionManager.openNotificationPolicySettings()) }
                    )
                    PermissionRow(
                        name = "Battery Optimization Exemption",
                        granted = uiState.isIgnoringBattery,
                        onClickFix = { context.startActivity(viewModel.permissionManager.openBatteryOptimizationSettings()) }
                    )
                }
            }

            Text("Privacy & Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "All location evaluation and sound triggers happen 100% locally on your device. Your exact coordinates are never continuously uploaded or tracked.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    name: String,
    granted: Boolean,
    onClickFix: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = if (granted) EmeraldGreen40 else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium)
        }
        if (!granted) {
            TextButton(onClick = onClickFix) {
                Text("Enable")
            }
        }
    }
}
