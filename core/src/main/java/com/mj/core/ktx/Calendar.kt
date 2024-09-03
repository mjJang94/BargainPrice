package com.mj.core.ktx

import java.util.Calendar

fun Calendar(timeInMillis: Long): Calendar =
    Calendar.getInstance().apply { this@apply.timeInMillis = timeInMillis }

fun Calendar.startOfNextDay() = this.apply {
    add(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.lastWeek() = this.apply {
    add(Calendar.DAY_OF_YEAR, -7)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

