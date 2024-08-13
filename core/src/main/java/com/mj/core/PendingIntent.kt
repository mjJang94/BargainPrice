package com.mj.core

import android.app.PendingIntent
import android.os.Build

fun flags() = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    true -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    else -> PendingIntent.FLAG_UPDATE_CURRENT
}
