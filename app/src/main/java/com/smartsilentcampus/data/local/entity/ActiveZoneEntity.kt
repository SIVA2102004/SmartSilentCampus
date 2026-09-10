package com.smartsilentcampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartsilentcampus.domain.model.ActiveZone

@Entity(tableName = "active_zones")
data class ActiveZoneEntity(
    @PrimaryKey
    val locationId: String,
    val enteredAt: Long,
    val priority: Int,
    val profileId: String
) {
    fun toDomain(): ActiveZone = ActiveZone(
        locationId = locationId,
        enteredAt = enteredAt,
        priority = priority,
        profileId = profileId
    )

    companion object {
        fun fromDomain(zone: ActiveZone): ActiveZoneEntity = ActiveZoneEntity(
            locationId = zone.locationId,
            enteredAt = zone.enteredAt,
            priority = zone.priority,
            profileId = zone.profileId
        )
    }
}
