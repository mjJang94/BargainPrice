package com.mj.core.notification

import android.app.PendingIntent

sealed class NotificationType {
    data class Simple(val title: String, val message: String) : NotificationType()
    data class Action(val title: String, val message: String, val actionText: String, val actionIntent: PendingIntent) : NotificationType()
    data class Progress(val title: String, val message: String, val progress: Int) : NotificationType()
}