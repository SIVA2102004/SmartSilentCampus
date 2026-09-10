package com.smartsilentcampus.presentation.testmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsilentcampus.domain.model.CampusLocation
import com.smartsilentcampus.domain.repository.ActiveZoneRepository
import com.smartsilentcampus.domain.repository.LocationRepository
import com.smartsilentcampus.domain.usecase.AutomationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TestModeUiState(
    val locations: List<CampusLocation> = emptyList(),
    val activeZoneIds: Set<String> = emptySet(),
    val lastActionLog: String = "Ready for geofence simulation"
)

@HiltViewModel
class TestModeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val activeZoneRepository: ActiveZoneRepository,
    private val automationEngine: AutomationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestModeUiState())
    val uiState: StateFlow<TestModeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val locs = locationRepository.getEnabledLocations()
            val zones = activeZoneRepository.getAllActiveZones().map { it.locationId }.toSet()
            _uiState.value = TestModeUiState(locations = locs, activeZoneIds = zones)
        }
    }

    fun simulateEnter(location: CampusLocation) {
        viewModelScope.launch {
            automationEngine.handleEnter(location.id)
            val updatedZones = activeZoneRepository.getAllActiveZones().map { it.locationId }.toSet()
            _uiState.value = _uiState.value.copy(
                activeZoneIds = updatedZones,
                lastActionLog = "Simulated ENTER into ${location.name}. Profile applied."
            )
        }
    }

    fun simulateExit(location: CampusLocation) {
        viewModelScope.launch {
            automationEngine.handleExit(location.id)
            val updatedZones = activeZoneRepository.getAllActiveZones().map { it.locationId }.toSet()
            _uiState.value = _uiState.value.copy(
                activeZoneIds = updatedZones,
                lastActionLog = "Simulated EXIT from ${location.name}."
            )
        }
    }
}
