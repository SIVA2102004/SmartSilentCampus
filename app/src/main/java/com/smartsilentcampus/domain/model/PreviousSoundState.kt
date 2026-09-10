package com.smartsilentcampus.domain.model

data class PreviousSoundState(
    val id: Long = 1L,
    val ringerMode: Int,
    val ringVolume: Int,
    val notificationVolume: Int,
    val mediaVolume: Int,
    val alarmVolume: Int,
    val systemVolume: Int,
    val vibrationSetting: Int,
    val capturedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
