package com.smartsilentcampus.presentation.testmode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsilentcampus.presentation.theme.AmberOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestModeScreen(
    viewModel: TestModeViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(containerColor = AmberOrange) {
                            Text("DEV / TEST MODE", color = Color.White)
                        }
                    }
                },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Geofence Simulator", fontWeight = FontWeight.Bold)
                    Text(
                        "Test multi-zone overlap and previous sound restoration immediately without traveling.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status: ${uiState.lastActionLog}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text("Locations (${uiState.locations.size})", fontWeight = FontWeight.Bold)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.locations, key = { it.id }) { loc ->
                    val isInside = uiState.activeZoneIds.contains(loc.id)
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(loc.name, fontWeight = FontWeight.Bold)
                                Text("Priority: ${loc.priority} • ${loc.type.displayName}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = if (isInside) "Status: INSIDE" else "Status: OUTSIDE",
                                    color = if (isInside) MaterialTheme.colorScheme.primary else Color.Gray,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.simulateEnter(loc) },
                                    enabled = !isInside
                                ) {
                                    Text("Enter")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.simulateExit(loc) },
                                    enabled = isInside
                                ) {
                                    Text("Exit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
