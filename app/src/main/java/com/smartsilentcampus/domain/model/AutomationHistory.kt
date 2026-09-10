package com.smartsilentcampus.domain.model

enum class GeofenceEventType {
    ENTER,
    EXIT,
    DWELL,
    UNKNOWN
}

data class AutomationHistory(
    val id: Long = 0,
    val locationId: String,
    val locationName: String,
    val eventType: GeofenceEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val appliedProfileName: String,
    val previousProfileName: String? = null,
    val success: Boolean = true,
    val errorMessage: String? = null
)
