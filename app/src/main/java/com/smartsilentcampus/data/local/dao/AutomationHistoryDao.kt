package com.smartsilentcampus.data.local.dao

import androidx.room.*
import com.smartsilentcampus.data.local.entity.AutomationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationHistoryDao {
    @Query("SELECT * FROM automation_history ORDER BY timestamp DESC")
    fun getAllHistoryFlow(): Flow<List<AutomationHistoryEntity>>

    @Query("SELECT * FROM automation_history WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getHistorySinceFlow(sinceTimestamp: Long): Flow<List<AutomationHistoryEntity>>

    @Insert
    suspend fun insertHistory(history: AutomationHistoryEntity)

    @Query("DELETE FROM automation_history")
    suspend fun clearAll()

    @Query("DELETE FROM automation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM automation_history WHERE timestamp >= :sinceTimestamp")
    suspend fun countSince(sinceTimestamp: Long): Int
}
