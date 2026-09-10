package com.smartsilentcampus.sound

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.smartsilentcampus.domain.model.PreviousSoundState
import com.smartsilentcampus.domain.model.SoundProfile
import com.smartsilentcampus.domain.model.SupportedRingerMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val TAG = "SoundController"
    }

    /**
     * Checks if the app has permission to modify Do Not Disturb / Notification Policy.
     * On Android 6.0+ (API 23+), modifying silent/DND mode requires Notification Policy Access.
     */
    fun hasNotificationPolicyAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.isNotificationPolicyAccessGranted
        } else {
            true
        }
    }

    /**
     * Captures current audio settings for rings, notifications, media, alarm, system streams and ringer mode.
     */
    fun captureCurrentSoundState(): PreviousSoundState {
        val ringerMode = audioManager.ringerMode
        val ringVol = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        val notifVol = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        val mediaVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val alarmVol = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        val sysVol = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)

        Log.d(TAG, "Captured sound state: ringerMode=$ringerMode, ring=$ringVol, notif=$notifVol, media=$mediaVol")

        return PreviousSoundState(
            id = 1L,
            ringerMode = ringerMode,
            ringVolume = ringVol,
            notificationVolume = notifVol,
            mediaVolume = mediaVol,
            alarmVolume = alarmVol,
            systemVolume = sysVol,
            vibrationSetting = if (ringerMode == AudioManager.RINGER_MODE_VIBRATE) 1 else 0,
            capturedAt = System.currentTimeMillis(),
            isActive = true
        )
    }

    /**
     * Applies the requested sound profile.
     * Respects Android API restrictions and gracefully falls back to VIBRATE if DND/SILENT access is denied.
     */
    fun applySoundProfile(profile: SoundProfile): Result<Unit> {
        return runCatching {
            val hasDndAccess = hasNotificationPolicyAccess()

            when (profile.ringerMode) {
                SupportedRingerMode.SILENT -> {
                    if (hasDndAccess) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    } else {
                        // Fallback to VIBRATE if DND policy access is not granted
                        Log.w(TAG, "Notification Policy Access missing. Falling back to VIBRATE mode.")
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    }
                }
                SupportedRingerMode.VIBRATE -> {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                }
                SupportedRingerMode.NORMAL -> {
                    if (hasDndAccess || audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    }
                }
                SupportedRingerMode.DO_NOT_DISTURB -> {
                    if (hasDndAccess) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                        }
                    } else {
                        Log.w(TAG, "DND requested but permission not granted; falling back to VIBRATE.")
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    }
                }
            }

            // Adjust specific stream volumes if configured
            applyStreamVolume(AudioManager.STREAM_RING, profile.ringVolumePercent)
            applyStreamVolume(AudioManager.STREAM_NOTIFICATION, profile.notificationVolumePercent)
            applyStreamVolume(AudioManager.STREAM_MUSIC, profile.mediaVolumePercent)
            applyStreamVolume(AudioManager.STREAM_ALARM, profile.alarmVolumePercent)
            applyStreamVolume(AudioManager.STREAM_SYSTEM, profile.systemVolumePercent)

            Log.i(TAG, "Successfully applied sound profile: ${profile.name}")
        }
    }

    /**
     * Restores previous sound state safely.
     */
    fun restorePreviousSoundState(state: PreviousSoundState): Result<Unit> {
        return runCatching {
            val hasDndAccess = hasNotificationPolicyAccess()

            // Reset interruption filter if changed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasDndAccess) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }

            if (hasDndAccess || state.ringerMode != AudioManager.RINGER_MODE_SILENT) {
                audioManager.ringerMode = state.ringerMode
            }

            // Restore stream volumes safely within max stream limits
            restoreStreamExact(AudioManager.STREAM_RING, state.ringVolume)
            restoreStreamExact(AudioManager.STREAM_NOTIFICATION, state.notificationVolume)
            restoreStreamExact(AudioManager.STREAM_MUSIC, state.mediaVolume)
            restoreStreamExact(AudioManager.STREAM_ALARM, state.alarmVolume)
            restoreStreamExact(AudioManager.STREAM_SYSTEM, state.systemVolume)

            Log.i(TAG, "Restored previous sound state: ringerMode=${state.ringerMode}")
        }
    }

    fun getCurrentRingerMode(): SupportedRingerMode {
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> SupportedRingerMode.SILENT
            AudioManager.RINGER_MODE_VIBRATE -> SupportedRingerMode.VIBRATE
            AudioManager.RINGER_MODE_NORMAL -> SupportedRingerMode.NORMAL
            else -> SupportedRingerMode.NORMAL
        }
    }

    private fun applyStreamVolume(streamType: Int, volumePercent: Int) {
        if (volumePercent < 0) return // keep unchanged
        try {
            val max = audioManager.getStreamMaxVolume(streamType)
            val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                audioManager.getStreamMinVolume(streamType)
            } else 0
            val target = min + ((max - min) * (volumePercent.coerceIn(0, 100) / 100f)).toInt()
            audioManager.setStreamVolume(streamType, target, 0)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while setting stream $streamType: ${e.message}")
        }
    }

    private fun restoreStreamExact(streamType: Int, targetVolume: Int) {
        try {
            val max = audioManager.getStreamMaxVolume(streamType)
            val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                audioManager.getStreamMinVolume(streamType)
            } else 0
            val safeVolume = targetVolume.coerceIn(min, max)
            audioManager.setStreamVolume(streamType, safeVolume, 0)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while restoring stream $streamType: ${e.message}")
        }
    }
}
