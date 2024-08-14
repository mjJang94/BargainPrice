package com.mj.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mj.core.flags
import com.mj.core.perm.PermissionHelper
import com.mj.core.timeFormatDebugFull
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class AlarmHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionHelper: PermissionHelper,
) {

    companion object {
        private const val THRESHOLD = 500L
    }

    sealed interface ComponentType {
        data object Service : ComponentType
        data object Receiver : ComponentType
    }

    private val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun getAlarmPendingIntent(type: ComponentType, intent: Intent): PendingIntent =
        when (type) {
            is ComponentType.Service -> PendingIntent.getService(context, 0, intent, flags())
            is ComponentType.Receiver -> PendingIntent.getBroadcast(context, 0, intent, flags())
        }


    fun set(triggerTime: Long, type: ComponentType, intent: Intent) {
        if (!permissionHelper.checkExactAlarmPerm()) {
            return Timber.w("set(${this::class.simpleName}): Permission not granted, SCHEDULE_EXACT_ALARM")
        }
        val alarmIntent = getAlarmPendingIntent(type, intent)

        val actualTime = triggerTime + THRESHOLD
        val info = AlarmManager.AlarmClockInfo(actualTime, alarmIntent)
        am.setAlarmClock(info, alarmIntent)

        Timber.d("alarm on ${triggerTime.timeFormatDebugFull()} + threshold=${THRESHOLD}ms")

    }

    fun cancel(type: ComponentType, intent: Intent) {
        val alarmIntent = getAlarmPendingIntent(type, intent)
        am.cancel(alarmIntent)
        Timber.d("cancel(${this::class.simpleName}): canceled")
    }
}