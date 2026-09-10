package com.smartsilentcampus.data.local.dao

import androidx.room.*
import com.smartsilentcampus.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY priority DESC, name ASC")
    fun getAllLocationsFlow(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE isEnabled = 1 ORDER BY priority DESC")
    suspend fun getEnabledLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocationById(id: String)

    @Query("UPDATE locations SET isEnabled = :isEnabled, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateEnabledStatus(id: String, isEnabled: Boolean, timestamp: Long)

    @Query("SELECT COUNT(*) FROM locations WHERE isEnabled = 1")
    fun getEnabledLocationCountFlow(): Flow<Int>
}
