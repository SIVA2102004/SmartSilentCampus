package com.smartsilentcampus.data.local.dao

import androidx.room.*
import com.smartsilentcampus.data.local.entity.SoundProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundProfileDao {
    @Query("SELECT * FROM sound_profiles ORDER BY isDefault DESC, name ASC")
    fun getAllProfilesFlow(): Flow<List<SoundProfileEntity>>

    @Query("SELECT * FROM sound_profiles WHERE id = :id")
    suspend fun getProfileById(id: String): SoundProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SoundProfileEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(profiles: List<SoundProfileEntity>)

    @Update
    suspend fun updateProfile(profile: SoundProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: SoundProfileEntity)

    @Query("SELECT COUNT(*) FROM sound_profiles")
    suspend fun count(): Int
}
