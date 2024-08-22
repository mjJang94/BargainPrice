package com.mj.core.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mj.core.R
import com.mj.core.flags

sealed class NotificationType(
    val title: String,
    val message: String,
    val actionText: String?,
    val pendingIntent: PendingIntent?,
) {
    data class RefreshSuccess(
        private val context: Context,
        private val intent: Intent,
    ) : NotificationType(
        title = context.getString(R.string.noti_refresh_success_title),
        message = context.getString(R.string.noti_refresh_success_label),
        actionText = context.getString(R.string.noti_refresh_success_confirm),
        pendingIntent = PendingIntent.getActivity(context, 0, intent, flags()),
    )

    data class RefreshFailure(
        private val context: Context,
        private val reason: String,
    ) : NotificationType(
        title = context.getString(R.string.noti_refresh_fail_title),
        message = reason,
        actionText = null,
        pendingIntent = null,
    )
}

