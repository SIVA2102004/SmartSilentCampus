package com.smartsilentcampus.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.smartsilentcampus.domain.usecase.AutomationEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var automationEngine: AutomationEngine

    companion object {
        private const val TAG = "GeofenceReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: run {
            Log.e(TAG, "GeofencingEvent is null in broadcast intent.")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, "Geofencing error code: ${geofencingEvent.errorCode} -> $errorMessage")
            return
        }

        val transitionType = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences ?: emptyList()

        Log.i(TAG, "Received transition $transitionType for ${triggeringGeofences.size} geofences.")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (geofence in triggeringGeofences) {
                    val locationId = geofence.requestId
                    when (transitionType) {
                        Geofence.GEOFENCE_TRANSITION_ENTER, Geofence.GEOFENCE_TRANSITION_DWELL -> {
                            Log.d(TAG, "Handling ENTER/DWELL for $locationId")
                            automationEngine.handleEnter(locationId)
                        }
                        Geofence.GEOFENCE_TRANSITION_EXIT -> {
                            Log.d(TAG, "Handling EXIT for $locationId")
                            automationEngine.handleExit(locationId)
                        }
                        else -> {
                            Log.w(TAG, "Unhandled transition type: $transitionType")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling geofence transition: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
