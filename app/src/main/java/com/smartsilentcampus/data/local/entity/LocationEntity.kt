package com.smartsilentcampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartsilentcampus.domain.model.CampusLocation
import com.smartsilentcampus.domain.model.LocationType

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val priority: Int,
    val profileId: String,
    val isEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): CampusLocation {
        val locationType = try {
            LocationType.valueOf(type)
        } catch (e: Exception) {
            LocationType.CUSTOM
        }
        return CampusLocation(
            id = id,
            name = name,
            type = locationType,
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            priority = priority,
            profileId = profileId,
            isEnabled = isEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(location: CampusLocation): LocationEntity {
            return LocationEntity(
                id = location.id,
                name = location.name,
                type = location.type.name,
                latitude = location.latitude,
                longitude = location.longitude,
                radiusMeters = location.radiusMeters,
                priority = location.priority,
                profileId = location.profileId,
                isEnabled = location.isEnabled,
                createdAt = location.createdAt,
                updatedAt = location.updatedAt
            )
        }
    }
}
