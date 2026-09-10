package com.smartsilentcampus.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsilentcampus.data.local.database.AppPreferencesDataStore
import com.smartsilentcampus.permissions.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isAutomationEnabled: Boolean = true,
    val isEntryNotificationEnabled: Boolean = true,
    val isExitNotificationEnabled: Boolean = true,
    val hasFineLocation: Boolean = false,
    val hasBackgroundLocation: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val hasDndAccess: Boolean = false,
    val isIgnoringBattery: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesDataStore: AppPreferencesDataStore,
    val permissionManager: PermissionManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        appPreferencesDataStore.isAutomationEnabled,
        appPreferencesDataStore.isEntryNotificationEnabled,
        appPreferencesDataStore.isExitNotificationEnabled
    ) { auto, entryNotif, exitNotif ->
        SettingsUiState(
            isAutomationEnabled = auto,
            isEntryNotificationEnabled = entryNotif,
            isExitNotificationEnabled = exitNotif,
            hasFineLocation = permissionManager.hasFineLocationPermission(),
            hasBackgroundLocation = permissionManager.hasBackgroundLocationPermission(),
            hasNotificationPermission = permissionManager.hasNotificationPermission(),
            hasDndAccess = permissionManager.hasNotificationPolicyAccess(),
            isIgnoringBattery = permissionManager.isIgnoringBatteryOptimizations()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun toggleAutomation(enabled: Boolean) {
        viewModelScope.launch { appPreferencesDataStore.setAutomationEnabled(enabled) }
    }

    fun toggleEntryNotification(enabled: Boolean) {
        viewModelScope.launch { appPreferencesDataStore.setEntryNotificationEnabled(enabled) }
    }

    fun toggleExitNotification(enabled: Boolean) {
        viewModelScope.launch { appPreferencesDataStore.setExitNotificationEnabled(enabled) }
    }
}
