package com.mindbloom.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mindbloom.app.MainActivity
import com.mindbloom.app.R

/**
 * Everything to do with posting the daily reminder. Kept in one place so the
 * worker only has to describe *what* to say, not how to say it.
 */
object NotificationHelper {

    const val CHANNEL_ID = "mindbloom_reminders"
    private const val NOTIFICATION_ID = 1001

    /** Safe to call repeatedly; creating an existing channel is a no-op. */
    fun createChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "A nudge to log your mood and finish today's habits."
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    /**
     * Posts the reminder. Returns false when the OS would drop it anyway —
     * either the runtime permission is missing or the user turned the channel
     * off in system settings.
     */
    fun showReminder(context: Context, title: String, body: String): Boolean {
        if (!canPostNotifications(context)) return false

        val openApp = PendingIntent.getActivity(
            context,
            0,
            android.content.Intent(context, MainActivity::class.java).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            true
        } catch (security: SecurityException) {
            // The permission was revoked between the check and the post.
            false
        }
    }

    fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
