package com.smartsilentcampus.presentation.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsilentcampus.domain.model.CampusLocation
import com.smartsilentcampus.domain.model.LocationType
import com.smartsilentcampus.domain.model.SoundProfile
import com.smartsilentcampus.domain.repository.LocationRepository
import com.smartsilentcampus.domain.repository.SoundProfileRepository
import com.smartsilentcampus.geofence.GeofenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class LocationsUiState(
    val locations: List<CampusLocation> = emptyList(),
    val availableProfiles: List<SoundProfile> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val soundProfileRepository: SoundProfileRepository,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                locationRepository.getAllLocationsFlow(),
                soundProfileRepository.getAllProfilesFlow()
            ) { locs, profiles ->
                LocationsUiState(
                    locations = locs,
                    availableProfiles = profiles,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleLocation(locationId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            locationRepository.toggleLocationEnabled(locationId, isEnabled)
            val updatedList = locationRepository.getEnabledLocations()
            geofenceManager.registerGeofences(updatedList)
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            locationRepository.deleteLocation(locationId)
            geofenceManager.removeGeofence(locationId)
        }
    }

    fun saveNewLocation(
        name: String,
        type: LocationType,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        profileId: String,
        priority: Int
    ) {
        viewModelScope.launch {
            val location = CampusLocation(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                priority = priority,
                profileId = profileId,
                isEnabled = true
            )
            locationRepository.saveLocation(location)
            val updated = locationRepository.getEnabledLocations()
            geofenceManager.registerGeofences(updated)
        }
    }
}
