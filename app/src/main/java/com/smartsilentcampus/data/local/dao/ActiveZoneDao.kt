package com.smartsilentcampus.data.local.dao

import androidx.room.*
import com.smartsilentcampus.data.local.entity.ActiveZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveZoneDao {
    @Query("SELECT * FROM active_zones ORDER BY priority DESC")
    fun getAllActiveZonesFlow(): Flow<List<ActiveZoneEntity>>

    @Query("SELECT * FROM active_zones ORDER BY priority DESC")
    suspend fun getAllActiveZones(): List<ActiveZoneEntity>

    @Query("SELECT * FROM active_zones WHERE locationId = :locationId LIMIT 1")
    suspend fun getActiveZone(locationId: String): ActiveZoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: ActiveZoneEntity)

    @Query("DELETE FROM active_zones WHERE locationId = :locationId")
    suspend fun removeZone(locationId: String)

    @Query("DELETE FROM active_zones")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM active_zones")
    suspend fun count(): Int

    @Query("SELECT * FROM active_zones ORDER BY priority DESC LIMIT 1")
    suspend fun getHighestPriorityActiveZone(): ActiveZoneEntity?
}
