package com.smartsilentcampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartsilentcampus.domain.model.SoundProfile
import com.smartsilentcampus.domain.model.SupportedRingerMode

@Entity(tableName = "sound_profiles")
data class SoundProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val ringerMode: String,
    val ringVolumePercent: Int,
    val notificationVolumePercent: Int,
    val mediaVolumePercent: Int,
    val alarmVolumePercent: Int,
    val systemVolumePercent: Int,
    val isVibrationEnabled: Boolean,
    val isDndEnabled: Boolean,
    val isDefault: Boolean
) {
    fun toDomain(): SoundProfile {
        val mode = try {
            SupportedRingerMode.valueOf(ringerMode)
        } catch (e: Exception) {
            SupportedRingerMode.SILENT
        }
        return SoundProfile(
            id = id,
            name = name,
            ringerMode = mode,
            ringVolumePercent = ringVolumePercent,
            notificationVolumePercent = notificationVolumePercent,
            mediaVolumePercent = mediaVolumePercent,
            alarmVolumePercent = alarmVolumePercent,
            systemVolumePercent = systemVolumePercent,
            isVibrationEnabled = isVibrationEnabled,
            isDndEnabled = isDndEnabled,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromDomain(profile: SoundProfile): SoundProfileEntity {
            return SoundProfileEntity(
                id = profile.id,
                name = profile.name,
                ringerMode = profile.ringerMode.name,
                ringVolumePercent = profile.ringVolumePercent,
                notificationVolumePercent = profile.notificationVolumePercent,
                mediaVolumePercent = profile.mediaVolumePercent,
                alarmVolumePercent = profile.alarmVolumePercent,
                systemVolumePercent = profile.systemVolumePercent,
                isVibrationEnabled = profile.isVibrationEnabled,
                isDndEnabled = profile.isDndEnabled,
                isDefault = profile.isDefault
            )
        }
    }
}
