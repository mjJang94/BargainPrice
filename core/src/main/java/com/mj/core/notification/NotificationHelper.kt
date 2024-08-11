package com.mj.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "example_channel_id"
    private val CHANNEL_NAME = "Example Channel"
    private val CHANNEL_DESCRIPTION = "This is an example channel for notifications"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(notificationType: NotificationType) {
        val notificationBuilder = when (notificationType) {
            is NotificationType.Simple -> buildSimpleNotification(notificationType)
            is NotificationType.Action -> buildActionNotification(notificationType)
            is NotificationType.Progress -> buildProgressNotification(notificationType)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationType.hashCode(), notificationBuilder.build())
    }

    private fun buildSimpleNotification(type: NotificationType.Simple): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(type.title)
            .setContentText(type.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    private fun buildActionNotification(type: NotificationType.Action): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(type.title)
            .setContentText(type.message)
            .addAction(android.R.drawable.ic_menu_view, type.actionText, type.actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    private fun buildProgressNotification(type: NotificationType.Progress): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(type.title)
            .setContentText(type.message)
            .setProgress(100, type.progress, false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }
}