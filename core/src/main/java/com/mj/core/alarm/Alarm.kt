package com.mj.core.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.mj.core.perm.Perm.checkExactAlarmPerm
import com.mj.core.timeFormatDebugFull
import timber.log.Timber

sealed class Alarm(
    private val getAlarmManager: () -> AlarmManager?,
    private val pending: () -> PendingIntent,
    private val permCheck: () -> Boolean,
) {

    companion object {
        private const val THRESHOLD = 500L
    }

    //여러개 상품을 예약걸꺼면 requestCode를 분리해야함, 아니면 하나의 알림 안에서 여러개를 갱신하던가
    data class PriceCheck(
        private val context: Context,
        private val intent: Intent
    ) : Alarm(
        { context.getSystemService() as AlarmManager? },
        { PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE) },
        { context.checkExactAlarmPerm() },
    )

    @SuppressLint("ScheduleExactAlarm")
    fun set(triggerTime: Long) {
        val am = getAlarmManager() ?: return

        //퍼미션 체크 들어가야 함
        if (!permCheck()) {
            return Timber.w("set(${this::class.simpleName}): Permission not granted, SCHEDULE_EXACT_ALARM")
        }

        val intent = pending()

        val actualTime = triggerTime + THRESHOLD
        val info = AlarmManager.AlarmClockInfo(actualTime, intent)
        am.setAlarmClock(info, intent)

        Timber.d("set(${this::class.simpleName}): alarm on ${triggerTime.timeFormatDebugFull()} + threshold=${THRESHOLD}ms")
    }

    fun cancel() {
        val am = getAlarmManager() ?: return
        am.cancel(pending())

        Timber.d("cancel(${this::class.simpleName}): canceled")
    }
}