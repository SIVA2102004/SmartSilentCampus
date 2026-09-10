package com.smartsilentcampus.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.smartsilentcampus.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartSilentNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_AUTOMATION = "channel_silent_automation"
        const val CHANNEL_RESTORE = "channel_silent_restore"
        const val CHANNEL_WARNINGS = "channel_silent_warnings"
        const val CHANNEL_STATUS = "channel_silent_status"

        const val NOTIFICATION_ID_STATUS = 1001
        const val NOTIFICATION_ID_ENTER = 2001
        const val NOTIFICATION_ID_EXIT = 2002
        const val NOTIFICATION_ID_WARNING = 3001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autoChannel = NotificationChannel(
                CHANNEL_AUTOMATION,
                "Silent Mode Automation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when phone automatically enters silent mode inside a campus zone."
                enableVibration(true)
            }

            val restoreChannel = NotificationChannel(
                CHANNEL_RESTORE,
                "Sound Restoration",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when phone sound settings are restored after leaving a campus zone."
            }

            val warningChannel = NotificationChannel(
                CHANNEL_WARNINGS,
                "Automation Alerts & Permissions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies about missing permissions or location provider issues."
            }

            val statusChannel = NotificationChannel(
                CHANNEL_STATUS,
                "Monitoring Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Displays background monitoring status."
            }

            notificationManager.createNotificationChannels(
                listOf(autoChannel, restoreChannel, warningChannel, statusChannel)
            )
        }
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showSilentActivatedNotification(locationName: String, profileName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_AUTOMATION)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .setContentTitle("Silent Mode Activated")
            .setContentText("Entered $locationName. $profileName has been applied.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("You entered $locationName.\nYour phone has been automatically switched to $profileName.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getMainActivityPendingIntent())
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ENTER, notification)
    }

    fun showSoundRestoredNotification(locationName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_RESTORE)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentTitle("Normal Sound Restored")
            .setContentText("You left $locationName. Your previous sound settings have been restored.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityPendingIntent())
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_EXIT, notification)
    }

    fun showPermissionWarningNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_WARNINGS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getMainActivityPendingIntent())
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_WARNING, notification)
    }

    fun showMonitoringStatus(locationCount: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_STATUS)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("SmartSilent Campus Active")
            .setContentText("Monitoring $locationCount campus locations in background.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(getMainActivityPendingIntent())
            .build()

        notificationManager.notify(NOTIFICATION_ID_STATUS, notification)
    }

    fun cancelStatusNotification() {
        notificationManager.cancel(NOTIFICATION_ID_STATUS)
    }
}
