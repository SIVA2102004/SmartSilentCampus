package com.smartsilentcampus.data.local.dao

import androidx.room.*
import com.smartsilentcampus.data.local.entity.PreviousSoundStateEntity

@Dao
interface PreviousSoundStateDao {
    @Query("SELECT * FROM previous_sound_state WHERE id = 1 LIMIT 1")
    suspend fun getSavedState(): PreviousSoundStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveState(state: PreviousSoundStateEntity)

    @Query("DELETE FROM previous_sound_state WHERE id = 1")
    suspend fun clearState()
}
