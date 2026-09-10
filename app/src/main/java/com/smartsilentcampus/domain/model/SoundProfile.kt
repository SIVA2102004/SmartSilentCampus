package com.smartsilentcampus.domain.model

data class SoundProfile(
    val id: String,
    val name: String,
    val ringerMode: SupportedRingerMode,
    val ringVolumePercent: Int = -1, // -1 means keep unchanged
    val notificationVolumePercent: Int = -1,
    val mediaVolumePercent: Int = -1,
    val alarmVolumePercent: Int = -1,
    val systemVolumePercent: Int = -1,
    val isVibrationEnabled: Boolean = false,
    val isDndEnabled: Boolean = false,
    val isDefault: Boolean = false
) {
    companion object {
        const val DEFAULT_SILENT_ID = "profile_silent"
        const val DEFAULT_VIBRATE_ID = "profile_vibrate"
        const val DEFAULT_NORMAL_ID = "profile_normal"
        const val DEFAULT_LIBRARY_ID = "profile_library"
        const val DEFAULT_EXAM_ID = "profile_exam"

        fun defaultProfiles(): List<SoundProfile> = listOf(
            SoundProfile(
                id = DEFAULT_NORMAL_ID,
                name = "Normal",
                ringerMode = SupportedRingerMode.NORMAL,
                isVibrationEnabled = true,
                isDefault = true
            ),
            SoundProfile(
                id = DEFAULT_SILENT_ID,
                name = "Silent Mode",
                ringerMode = SupportedRingerMode.SILENT,
                ringVolumePercent = 0,
                notificationVolumePercent = 0,
                systemVolumePercent = 0,
                isVibrationEnabled = false,
                isDefault = true
            ),
            SoundProfile(
                id = DEFAULT_VIBRATE_ID,
                name = "Vibrate Only",
                ringerMode = SupportedRingerMode.VIBRATE,
                ringVolumePercent = 0,
                notificationVolumePercent = 0,
                isVibrationEnabled = true,
                isDefault = true
            ),
            SoundProfile(
                id = DEFAULT_LIBRARY_ID,
                name = "Library Mode",
                ringerMode = SupportedRingerMode.SILENT,
                ringVolumePercent = 0,
                notificationVolumePercent = 0,
                mediaVolumePercent = 20,
                isVibrationEnabled = true,
                isDefault = true
            ),
            SoundProfile(
                id = DEFAULT_EXAM_ID,
                name = "Exam Hall (Strict Silence)",
                ringerMode = SupportedRingerMode.DO_NOT_DISTURB,
                ringVolumePercent = 0,
                notificationVolumePercent = 0,
                systemVolumePercent = 0,
                mediaVolumePercent = 0,
                alarmVolumePercent = 0,
                isVibrationEnabled = false,
                isDndEnabled = true,
                isDefault = true
            )
        )
    }
}
