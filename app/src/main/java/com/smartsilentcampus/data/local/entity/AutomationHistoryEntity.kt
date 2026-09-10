package com.smartsilentcampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartsilentcampus.domain.model.AutomationHistory
import com.smartsilentcampus.domain.model.GeofenceEventType

@Entity(tableName = "automation_history")
data class AutomationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationId: String,
    val locationName: String,
    val eventType: String,
    val timestamp: Long,
    val appliedProfileName: String,
    val previousProfileName: String?,
    val success: Boolean,
    val errorMessage: String?
) {
    fun toDomain(): AutomationHistory {
        val type = try {
            GeofenceEventType.valueOf(eventType)
        } catch (e: Exception) {
            GeofenceEventType.UNKNOWN
        }
        return AutomationHistory(
            id = id,
            locationId = locationId,
            locationName = locationName,
            eventType = type,
            timestamp = timestamp,
            appliedProfileName = appliedProfileName,
            previousProfileName = previousProfileName,
            success = success,
            errorMessage = errorMessage
        )
    }

    companion object {
        fun fromDomain(history: AutomationHistory): AutomationHistoryEntity {
            return AutomationHistoryEntity(
                id = history.id,
                locationId = history.locationId,
                locationName = history.locationName,
                eventType = history.eventType.name,
                timestamp = history.timestamp,
                appliedProfileName = history.appliedProfileName,
                previousProfileName = history.previousProfileName,
                success = history.success,
                errorMessage = history.errorMessage
            )
        }
    }
}
