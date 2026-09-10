package com.smartsilentcampus.presentation.locations

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.smartsilentcampus.domain.model.LocationType
import com.smartsilentcampus.domain.model.SoundProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
    viewModel: LocationsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(LocationType.CLASSROOM) }
    var latitude by remember { mutableStateOf(12.9716) }
    var longitude by remember { mutableStateOf(77.5946) }
    var radiusMeters by remember { mutableFloatStateOf(100f) }
    var selectedProfileId by remember { mutableStateOf(SoundProfile.DEFAULT_SILENT_ID) }
    var priority by remember { mutableIntStateOf(selectedType.defaultPriority) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Silent Zone") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Location Name (e.g. CS Seminar Hall)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Location Type", fontWeight = FontWeight.Bold)
            ScrollableTabRow(
                selectedTabIndex = LocationType.values().indexOf(selectedType),
                edgePadding = 0.dp
            ) {
                LocationType.values().forEach { type ->
                    Tab(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            priority = type.defaultPriority
                        },
                        text = { Text(type.displayName) }
                    )
                }
            }

            Text("Geofence Radius: ${radiusMeters.toInt()} meters", fontWeight = FontWeight.Bold)
            Slider(
                value = radiusMeters,
                onValueChange = { radiusMeters = it },
                valueRange = 20f..500f,
                steps = 19
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(50f, 100f, 150f, 200f, 300f).forEach { r ->
                    FilterChip(
                        selected = radiusMeters == r,
                        onClick = { radiusMeters = r },
                        label = { Text("${r.toInt()}m") }
                    )
                }
            }

            @SuppressLint("MissingPermission")
            Button(
                onClick = {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                            if (loc != null) {
                                latitude = loc.latitude
                                longitude = loc.longitude
                            }
                        }
                    } catch (e: SecurityException) {
                        // Handled by permission flow
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use My Current Location")
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Coordinates: Lat ${"%.4f".format(latitude)}, Lon ${"%.4f".format(longitude)}", style = MaterialTheme.typography.bodySmall)
                    Text("Zone Priority: $priority (Higher priority takes precedence)", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text("Sound Profile", fontWeight = FontWeight.Bold)
            uiState.availableProfiles.forEach { profile ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedProfileId == profile.id,
                        onClick = { selectedProfileId = profile.id }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(profile.name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.saveNewLocation(
                            name = name,
                            type = selectedType,
                            latitude = latitude,
                            longitude = longitude,
                            radiusMeters = radiusMeters,
                            profileId = selectedProfileId,
                            priority = priority
                        )
                        onNavigateBack()
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Location")
            }
        }
    }
}
