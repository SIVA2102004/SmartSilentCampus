package com.smartsilentcampus.presentation.locations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartsilentcampus.presentation.components.LocationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    viewModel: LocationsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddLocation: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Locations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddLocation) {
                Icon(Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.locations, key = { it.id }) { loc ->
                LocationCard(
                    location = loc,
                    onToggle = { isEnabled -> viewModel.toggleLocation(loc.id, isEnabled) },
                    onDelete = { viewModel.deleteLocation(loc.id) },
                    onEdit = { /* Edit dialog or screen */ }
                )
            }
        }
    }
}
