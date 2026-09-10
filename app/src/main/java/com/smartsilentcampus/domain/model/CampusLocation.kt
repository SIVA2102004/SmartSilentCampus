package com.smartsilentcampus.domain.model

data class CampusLocation(
    val id: String,
    val name: String,
    val type: LocationType,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val priority: Int,
    val profileId: String,
    val isEnabled: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
