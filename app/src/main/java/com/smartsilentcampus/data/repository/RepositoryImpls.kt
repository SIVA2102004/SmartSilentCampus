package com.smartsilentcampus.data.repository

import com.smartsilentcampus.data.local.dao.*
import com.smartsilentcampus.data.local.entity.*
import com.smartsilentcampus.domain.model.*
import com.smartsilentcampus.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {
    override fun getAllLocationsFlow(): Flow<List<CampusLocation>> {
        return locationDao.getAllLocationsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEnabledLocations(): List<CampusLocation> {
        return locationDao.getEnabledLocations().map { it.toDomain() }
    }

    override suspend fun getLocationById(id: String): CampusLocation? {
        return locationDao.getLocationById(id)?.toDomain()
    }

    override suspend fun saveLocation(location: CampusLocation) {
        locationDao.insertLocation(LocationEntity.fromDomain(location))
    }

    override suspend fun deleteLocation(id: String) {
        locationDao.deleteLocationById(id)
    }

    override suspend fun toggleLocationEnabled(id: String, isEnabled: Boolean) {
        locationDao.updateEnabledStatus(id, isEnabled, System.currentTimeMillis())
    }

    override fun getEnabledLocationCountFlow(): Flow<Int> {
        return locationDao.getEnabledLocationCountFlow()
    }
}

@Singleton
class SoundProfileRepositoryImpl @Inject constructor(
    private val soundProfileDao: SoundProfileDao
) : SoundProfileRepository {
    override fun getAllProfilesFlow(): Flow<List<SoundProfile>> {
        return soundProfileDao.getAllProfilesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProfileById(id: String): SoundProfile? {
        return soundProfileDao.getProfileById(id)?.toDomain()
    }

    override suspend fun saveProfile(profile: SoundProfile) {
        soundProfileDao.insertProfile(SoundProfileEntity.fromDomain(profile))
    }

    override suspend fun deleteProfile(profile: SoundProfile) {
        soundProfileDao.deleteProfile(SoundProfileEntity.fromDomain(profile))
    }

    override suspend fun seedDefaultProfilesIfEmpty() {
        if (soundProfileDao.count() == 0) {
            val defaults = SoundProfile.defaultProfiles().map { SoundProfileEntity.fromDomain(it) }
            soundProfileDao.insertAll(defaults)
        }
    }
}

@Singleton
class SoundStateRepositoryImpl @Inject constructor(
    private val previousSoundStateDao: PreviousSoundStateDao
) : SoundStateRepository {
    override suspend fun getSavedState(): PreviousSoundState? {
        return previousSoundStateDao.getSavedState()?.toDomain()
    }

    override suspend fun saveState(state: PreviousSoundState) {
        previousSoundStateDao.saveState(PreviousSoundStateEntity.fromDomain(state))
    }

    override suspend fun clearState() {
        previousSoundStateDao.clearState()
    }
}

@Singleton
class ActiveZoneRepositoryImpl @Inject constructor(
    private val activeZoneDao: ActiveZoneDao
) : ActiveZoneRepository {
    override fun getAllActiveZonesFlow(): Flow<List<ActiveZone>> {
        return activeZoneDao.getAllActiveZonesFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAllActiveZones(): List<ActiveZone> {
        return activeZoneDao.getAllActiveZones().map { it.toDomain() }
    }

    override suspend fun addActiveZone(zone: ActiveZone) {
        activeZoneDao.insertZone(ActiveZoneEntity.fromDomain(zone))
    }

    override suspend fun removeActiveZone(locationId: String) {
        activeZoneDao.removeZone(locationId)
    }

    override suspend fun clearAllActiveZones() {
        activeZoneDao.clearAll()
    }

    override suspend fun getHighestPriorityActiveZone(): ActiveZone? {
        return activeZoneDao.getHighestPriorityActiveZone()?.toDomain()
    }

    override suspend fun getActiveZoneCount(): Int {
        return activeZoneDao.count()
    }
}

@Singleton
class AutomationHistoryRepositoryImpl @Inject constructor(
    private val historyDao: AutomationHistoryDao
) : AutomationHistoryRepository {
    override fun getAllHistoryFlow(): Flow<List<AutomationHistory>> {
        return historyDao.getAllHistoryFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun getHistorySinceFlow(sinceTimestamp: Long): Flow<List<AutomationHistory>> {
        return historyDao.getHistorySinceFlow(sinceTimestamp).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addHistory(history: AutomationHistory) {
        historyDao.insertHistory(AutomationHistoryEntity.fromDomain(history))
    }

    override suspend fun clearAllHistory() {
        historyDao.clearAll()
    }

    override suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteById(id)
    }

    override suspend fun countSince(sinceTimestamp: Long): Int {
        return historyDao.countSince(sinceTimestamp)
    }
}
