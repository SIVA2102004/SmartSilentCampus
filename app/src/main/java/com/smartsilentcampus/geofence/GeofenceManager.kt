package com.smartsilentcampus.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.smartsilentcampus.domain.model.CampusLocation
import com.smartsilentcampus.permissions.PermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    companion object {
        private const val TAG = "GeofenceManager"
        const val ACTION_GEOFENCE_EVENT = "com.smartsilentcampus.ACTION_GEOFENCE_EVENT"
        private const val GEOFENCE_EXPIRATION_MS = Geofence.NEVER_EXPIRE
        private const val DWELL_LOITERING_DELAY_MS = 15_000 // 15 seconds to prevent bouncing
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = ACTION_GEOFENCE_EVENT
        }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * Builds and registers geofences for all provided active locations.
     */
    @SuppressLint("MissingPermission")
    suspend fun registerGeofences(locations: List<CampusLocation>): Result<Unit> = runCatching {
        if (!permissionManager.hasFineLocationPermission()) {
            throw SecurityException("Fine location permission is missing.")
        }
        if (!permissionManager.hasBackgroundLocationPermission()) {
            Log.w(TAG, "Background location not granted; geofences might not trigger in background.")
        }

        val enabledLocations = locations.filter { it.isEnabled }
        if (enabledLocations.isEmpty()) {
            removeAllGeofences()
            return@runCatching
        }

        val geofenceList = enabledLocations.map { loc ->
            Geofence.Builder()
                .setRequestId(loc.id)
                .setCircularRegion(loc.latitude, loc.longitude, loc.radiusMeters)
                .setExpirationDuration(GEOFENCE_EXPIRATION_MS)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT or
                            Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .setLoiteringDelay(DWELL_LOITERING_DELAY_MS)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL)
            .addGeofences(geofenceList)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).await()
        Log.i(TAG, "Successfully registered ${geofenceList.size} geofences.")
    }

    suspend fun removeGeofence(locationId: String): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(listOf(locationId)).await()
        Log.i(TAG, "Removed geofence for location $locationId")
    }

    suspend fun removeAllGeofences(): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(geofencePendingIntent).await()
        Log.i(TAG, "Removed all registered geofences.")
    }
}
