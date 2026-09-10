package com.smartsilentcampus.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.smartsilentcampus.domain.repository.LocationRepository
import com.smartsilentcampus.geofence.GeofenceManager
import com.smartsilentcampus.notification.SmartSilentNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var geofenceManager: GeofenceManager

    @Inject
    lateinit var notificationManager: SmartSilentNotificationManager

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.i(TAG, "Device reboot or package replaced detected ($action). Restoring geofences...")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val enabledLocations = locationRepository.getEnabledLocations()
                    if (enabledLocations.isNotEmpty()) {
                        geofenceManager.registerGeofences(enabledLocations)
                        notificationManager.showMonitoringStatus(enabledLocations.size)
                        Log.i(TAG, "Re-registered ${enabledLocations.size} geofences after reboot.")
                    } else {
                        Log.i(TAG, "No enabled locations to re-register.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to restore geofences on boot: ${e.message}", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
