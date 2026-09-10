package com.smartsilentcampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartsilentcampus.domain.model.PreviousSoundState

@Entity(tableName = "previous_sound_state")
data class PreviousSoundStateEntity(
    @PrimaryKey
    val id: Long = 1L,
    val ringerMode: Int,
    val ringVolume: Int,
    val notificationVolume: Int,
    val mediaVolume: Int,
    val alarmVolume: Int,
    val systemVolume: Int,
    val vibrationSetting: Int,
    val capturedAt: Long,
    val isActive: Boolean
) {
    fun toDomain(): PreviousSoundState {
        return PreviousSoundState(
            id = id,
            ringerMode = ringerMode,
            ringVolume = ringVolume,
            notificationVolume = notificationVolume,
            mediaVolume = mediaVolume,
            alarmVolume = alarmVolume,
            systemVolume = systemVolume,
            vibrationSetting = vibrationSetting,
            capturedAt = capturedAt,
            isActive = isActive
        )
    }

    companion object {
        fun fromDomain(state: PreviousSoundState): PreviousSoundStateEntity {
            return PreviousSoundStateEntity(
                id = state.id,
                ringerMode = state.ringerMode,
                ringVolume = state.ringVolume,
                notificationVolume = state.notificationVolume,
                mediaVolume = state.mediaVolume,
                alarmVolume = state.alarmVolume,
                systemVolume = state.systemVolume,
                vibrationSetting = state.vibrationSetting,
                capturedAt = state.capturedAt,
                isActive = state.isActive
            )
        }
    }
}
