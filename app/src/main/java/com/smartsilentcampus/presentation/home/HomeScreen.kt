package com.smartsilentcampus.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsilentcampus.presentation.components.LocationCard
import com.smartsilentcampus.presentation.components.PermissionBanner
import com.smartsilentcampus.presentation.components.StatusCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddLocation: () -> Unit,
    onNavigateToLocations: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTestMode: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Smart Silent Campus",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your phone knows when to stay silent.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToTestMode) {
                        Icon(Icons.Default.BugReport, contentDescription = "Test Mode")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddLocation,
                icon = { Icon(Icons.Default.AddLocationAlt, contentDescription = null) },
                text = { Text("Add Location") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                PermissionBanner(
                    missingPermissions = uiState.missingPermissions,
                    onFixPermissions = onNavigateToSettings
                )
            }

            item {
                StatusCard(
                    ringerMode = uiState.currentRingerMode,
                    isAutomationEnabled = uiState.isAutomationEnabled,
                    isPaused = uiState.isAutomationPaused,
                    pausedMinutes = uiState.pausedRemainingMinutes,
                    activeZoneName = uiState.activeZoneName,
                    onEmergencyRestore = { viewModel.emergencyRestore(pauseDurationMinutes = 30) },
                    onResumeAutomation = { viewModel.resumeAutomation() }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Locations (${uiState.totalConfiguredLocations})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToLocations) {
                        Text("View All")
                    }
                }
            }

            if (uiState.recentLocations.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AddLocation,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Locations Configured",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Add your college campus, library or classroom to enable automated silent mode.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onNavigateToAddLocation) {
                                Text("Add First Location")
                            }
                        }
                    }
                }
            } else {
                items(uiState.recentLocations) { loc ->
                    LocationCard(
                        location = loc,
                        onToggle = {},
                        onDelete = {},
                        onEdit = onNavigateToLocations
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToProfiles,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sound Profiles")
                    }
                    OutlinedButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("History")
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
