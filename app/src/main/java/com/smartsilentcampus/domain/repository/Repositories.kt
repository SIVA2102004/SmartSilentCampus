package com.smartsilentcampus.domain.repository

import com.smartsilentcampus.domain.model.*
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocationsFlow(): Flow<List<CampusLocation>>
    suspend fun getEnabledLocations(): List<CampusLocation>
    suspend fun getLocationById(id: String): CampusLocation?
    suspend fun saveLocation(location: CampusLocation)
    suspend fun deleteLocation(id: String)
    suspend fun toggleLocationEnabled(id: String, isEnabled: Boolean)
    fun getEnabledLocationCountFlow(): Flow<Int>
}

interface SoundProfileRepository {
    fun getAllProfilesFlow(): Flow<List<SoundProfile>>
    suspend fun getProfileById(id: String): SoundProfile?
    suspend fun saveProfile(profile: SoundProfile)
    suspend fun deleteProfile(profile: SoundProfile)
    suspend fun seedDefaultProfilesIfEmpty()
}

interface SoundStateRepository {
    suspend fun getSavedState(): PreviousSoundState?
    suspend fun saveState(state: PreviousSoundState)
    suspend fun clearState()
}

interface ActiveZoneRepository {
    fun getAllActiveZonesFlow(): Flow<List<ActiveZone>>
    suspend fun getAllActiveZones(): List<ActiveZone>
    suspend fun addActiveZone(zone: ActiveZone)
    suspend fun removeActiveZone(locationId: String)
    suspend fun clearAllActiveZones()
    suspend fun getHighestPriorityActiveZone(): ActiveZone?
    suspend fun getActiveZoneCount(): Int
}

interface AutomationHistoryRepository {
    fun getAllHistoryFlow(): Flow<List<AutomationHistory>>
    fun getHistorySinceFlow(sinceTimestamp: Long): Flow<List<AutomationHistory>>
    suspend fun addHistory(history: AutomationHistory)
    suspend fun clearAllHistory()
    suspend fun deleteHistoryById(id: Long)
    suspend fun countSince(sinceTimestamp: Long): Int
}
