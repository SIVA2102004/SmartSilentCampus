package com.smartsilentcampus.domain.usecase

import android.util.Log
import com.smartsilentcampus.data.local.database.AppPreferencesDataStore
import com.smartsilentcampus.domain.model.*
import com.smartsilentcampus.domain.repository.*
import com.smartsilentcampus.notification.SmartSilentNotificationManager
import com.smartsilentcampus.sound.SoundController
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutomationEngine @Inject constructor(
    private val locationRepository: LocationRepository,
    private val soundProfileRepository: SoundProfileRepository,
    private val soundStateRepository: SoundStateRepository,
    private val activeZoneRepository: ActiveZoneRepository,
    private val historyRepository: AutomationHistoryRepository,
    private val soundController: SoundController,
    private val notificationManager: SmartSilentNotificationManager,
    private val appPreferencesDataStore: AppPreferencesDataStore
) {
    companion object {
        private const val TAG = "AutomationEngine"
    }

    /**
     * Handles ENTER geofence transition:
     * 1. Validates automation is enabled and not temporarily paused.
     * 2. Checks if this is the first active zone entered. If so, snapshots current sound state.
     * 3. Adds location to active zones.
     * 4. Evaluates priority among ALL currently active zones.
     * 5. Applies sound profile of the highest priority active zone.
     * 6. Records history and posts notification.
     */
    suspend fun handleEnter(locationId: String) {
        val isEnabled = appPreferencesDataStore.isAutomationEnabled.first()
        val pausedUntil = appPreferencesDataStore.automationPausedUntil.first()
        val now = System.currentTimeMillis()

        if (!isEnabled) {
            Log.d(TAG, "Automation is disabled. Skipping enter event for $locationId")
            return
        }
        if (pausedUntil > now) {
            Log.d(TAG, "Automation paused until $pausedUntil. Skipping enter event for $locationId")
            return
        }

        val location = locationRepository.getLocationById(locationId)
        if (location == null || !location.isEnabled) {
            Log.w(TAG, "Location $locationId not found or disabled.")
            return
        }

        val activeCount = activeZoneRepository.getActiveZoneCount()
        // If entering first zone, capture pristine sound state
        if (activeCount == 0) {
            val currentState = soundController.captureCurrentSoundState()
            soundStateRepository.saveState(currentState)
            Log.i(TAG, "Captured original sound state before applying zone profile: $currentState")
        }

        // Add to active zones
        val newActiveZone = ActiveZone(
            locationId = location.id,
            enteredAt = now,
            priority = location.priority,
            profileId = location.profileId
        )
        activeZoneRepository.addActiveZone(newActiveZone)

        // Find highest priority active zone
        val highestZone = activeZoneRepository.getHighestPriorityActiveZone() ?: newActiveZone
        val targetProfile = soundProfileRepository.getProfileById(highestZone.profileId)
            ?: SoundProfile.defaultProfiles().first { it.id == SoundProfile.DEFAULT_SILENT_ID }

        val result = soundController.applySoundProfile(targetProfile)
        val isSuccess = result.isSuccess
        val errorMsg = result.exceptionOrNull()?.message

        // Save history
        historyRepository.addHistory(
            AutomationHistory(
                locationId = location.id,
                locationName = location.name,
                eventType = GeofenceEventType.ENTER,
                timestamp = now,
                appliedProfileName = targetProfile.name,
                success = isSuccess,
                errorMessage = errorMsg
            )
        )

        // Notification
        if (isSuccess && appPreferencesDataStore.isEntryNotificationEnabled.first()) {
            notificationManager.showSilentActivatedNotification(location.name, targetProfile.name)
        }
    }

    /**
     * Handles EXIT geofence transition:
     * 1. Removes location from active zones.
     * 2. Checks if user remains in other active zones (nested geofences).
     * 3. If remaining zones exist: Switch to the highest priority among remaining zones.
     * 4. If NO remaining zones: Restore exact saved previous sound state and clear snapshot.
     * 5. Records history and posts notification.
     */
    suspend fun handleExit(locationId: String) {
        val location = locationRepository.getLocationById(locationId)
        val locName = location?.name ?: "Campus Location"
        val now = System.currentTimeMillis()

        activeZoneRepository.removeActiveZone(locationId)
        val remainingHighest = activeZoneRepository.getHighestPriorityActiveZone()

        if (remainingHighest != null) {
            // Still inside a parent or overlapping zone!
            val parentProfile = soundProfileRepository.getProfileById(remainingHighest.profileId)
                ?: SoundProfile.defaultProfiles().first { it.id == SoundProfile.DEFAULT_SILENT_ID }
            soundController.applySoundProfile(parentProfile)

            historyRepository.addHistory(
                AutomationHistory(
                    locationId = locationId,
                    locationName = locName,
                    eventType = GeofenceEventType.EXIT,
                    timestamp = now,
                    appliedProfileName = "Maintained ${parentProfile.name} (Active in nested zone)",
                    success = true
                )
            )
            Log.i(TAG, "Exited $locationId but remaining in zone ${remainingHighest.locationId}. Maintaining profile ${parentProfile.name}")
        } else {
            // Exited all silent zones! Restore previous sound state
            val savedState = soundStateRepository.getSavedState()
            var restoredSuccess = false
            var errorMsg: String? = null

            if (savedState != null) {
                val restoreResult = soundController.restorePreviousSoundState(savedState)
                restoredSuccess = restoreResult.isSuccess
                errorMsg = restoreResult.exceptionOrNull()?.message
                soundStateRepository.clearState()
            } else {
                // Fallback to normal mode if state wasn't captured
                val normalProfile = SoundProfile.defaultProfiles().first { it.id == SoundProfile.DEFAULT_NORMAL_ID }
                soundController.applySoundProfile(normalProfile)
                restoredSuccess = true
            }

            historyRepository.addHistory(
                AutomationHistory(
                    locationId = locationId,
                    locationName = locName,
                    eventType = GeofenceEventType.EXIT,
                    timestamp = now,
                    appliedProfileName = "Normal Sound Restored",
                    success = restoredSuccess,
                    errorMessage = errorMsg
                )
            )

            if (appPreferencesDataStore.isExitNotificationEnabled.first()) {
                notificationManager.showSoundRestoredNotification(locName)
            }
            Log.i(TAG, "Exited all zones. Normal sound restored successfully.")
        }
    }

    /**
     * Emergency restore: immediately restores saved sound state, clears active zones, and pauses automation.
     */
    suspend fun emergencyRestore(pauseDurationMs: Long = 30 * 60 * 1000L) {
        val savedState = soundStateRepository.getSavedState()
        if (savedState != null) {
            soundController.restorePreviousSoundState(savedState)
            soundStateRepository.clearState()
        } else {
            val normalProfile = SoundProfile.defaultProfiles().first { it.id == SoundProfile.DEFAULT_NORMAL_ID }
            soundController.applySoundProfile(normalProfile)
        }

        activeZoneRepository.clearAllActiveZones()
        if (pauseDurationMs > 0) {
            appPreferencesDataStore.pauseAutomationUntil(System.currentTimeMillis() + pauseDurationMs)
        }

        historyRepository.addHistory(
            AutomationHistory(
                locationId = "emergency",
                locationName = "Emergency Override",
                eventType = GeofenceEventType.EXIT,
                timestamp = System.currentTimeMillis(),
                appliedProfileName = "Emergency Normal Restored",
                success = true
            )
        )
    }
}
