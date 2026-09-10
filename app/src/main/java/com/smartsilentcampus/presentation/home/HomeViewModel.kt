package com.smartsilentcampus.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsilentcampus.data.local.database.AppPreferencesDataStore
import com.smartsilentcampus.domain.model.*
import com.smartsilentcampus.domain.repository.*
import com.smartsilentcampus.domain.usecase.AutomationEngine
import com.smartsilentcampus.geofence.GeofenceManager
import com.smartsilentcampus.permissions.PermissionManager
import com.smartsilentcampus.sound.SoundController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentRingerMode: SupportedRingerMode = SupportedRingerMode.NORMAL,
    val isAutomationEnabled: Boolean = true,
    val isAutomationPaused: Boolean = false,
    val pausedRemainingMinutes: Int = 0,
    val activeZoneCount: Int = 0,
    val activeZoneName: String? = null,
    val totalConfiguredLocations: Int = 0,
    val recentLocations: List<CampusLocation> = emptyList(),
    val missingPermissions: List<String> = emptyList(),
    val isGpsEnabled: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val activeZoneRepository: ActiveZoneRepository,
    private val soundController: SoundController,
    private val permissionManager: PermissionManager,
    private val geofenceManager: GeofenceManager,
    private val automationEngine: AutomationEngine,
    private val appPreferencesDataStore: AppPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeState()
        refreshStatus()
    }

    private fun observeState() {
        viewModelScope.launch {
            combine(
                locationRepository.getAllLocationsFlow(),
                activeZoneRepository.getAllActiveZonesFlow(),
                appPreferencesDataStore.isAutomationEnabled,
                appPreferencesDataStore.automationPausedUntil
            ) { locations, activeZones, isEnabled, pausedUntil ->
                val now = System.currentTimeMillis()
                val isPaused = pausedUntil > now
                val pausedMinutes = if (isPaused) ((pausedUntil - now) / (60 * 1000)).toInt() + 1 else 0

                val activeLocation = if (activeZones.isNotEmpty()) {
                    locations.find { it.id == activeZones.first().locationId }
                } else null

                val missing = mutableListOf<String>()
                if (!permissionManager.hasFineLocationPermission()) missing.add("Location Permission")
                if (!permissionManager.hasBackgroundLocationPermission()) missing.add("Background Location")
                if (!permissionManager.hasNotificationPermission()) missing.add("Notifications")
                if (!permissionManager.hasNotificationPolicyAccess()) missing.add("Do Not Disturb Access")

                HomeUiState(
                    currentRingerMode = soundController.getCurrentRingerMode(),
                    isAutomationEnabled = isEnabled,
                    isAutomationPaused = isPaused,
                    pausedRemainingMinutes = pausedMinutes,
                    activeZoneCount = activeZones.size,
                    activeZoneName = activeLocation?.name,
                    totalConfiguredLocations = locations.size,
                    recentLocations = locations.take(3),
                    missingPermissions = missing,
                    isGpsEnabled = permissionManager.isLocationServiceEnabled()
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refreshStatus() {
        _uiState.update {
            it.copy(
                currentRingerMode = soundController.getCurrentRingerMode(),
                isGpsEnabled = permissionManager.isLocationServiceEnabled()
            )
        }
    }

    fun toggleAutomation(enabled: Boolean) {
        viewModelScope.launch {
            appPreferencesDataStore.setAutomationEnabled(enabled)
            if (enabled) {
                appPreferencesDataStore.clearPause()
                val activeLocs = locationRepository.getEnabledLocations()
                geofenceManager.registerGeofences(activeLocs)
            } else {
                geofenceManager.removeAllGeofences()
                automationEngine.emergencyRestore(pauseDurationMs = 0)
            }
        }
    }

    fun emergencyRestore(pauseDurationMinutes: Int = 30) {
        viewModelScope.launch {
            automationEngine.emergencyRestore(pauseDurationMs = pauseDurationMinutes * 60 * 1000L)
            refreshStatus()
        }
    }

    fun resumeAutomation() {
        viewModelScope.launch {
            appPreferencesDataStore.clearPause()
            val activeLocs = locationRepository.getEnabledLocations()
            geofenceManager.registerGeofences(activeLocs)
            refreshStatus()
        }
    }
}
