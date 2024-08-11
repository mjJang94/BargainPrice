@file:JvmName("DebugTimeFormat")

package com.mj.core

import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun Long.timeFormatDebugFull(): String =
    format("yyyy-MM-dd HH:mm:ss")

fun Long.timeFormatDebugLong() =
    format("MM-dd HH:mm:ss")

fun Long.timeFormatDebugShort(): String =
    format("HH:mm:ss")

fun Long.timeFormatDebugDiff() =
    durationFormat("hh:mm:ss")


private val oneHour by lazy { 1L.hours.inWholeMilliseconds }
private val oneMinute by lazy { 1L.minutes.inWholeMilliseconds }
private val oneSecond by lazy { 1L.seconds.inWholeMilliseconds }

val Long.overHour get() = this >= oneHour
val Long.overMinute get() = this >= oneMinute
val Long.hasMinutes get() = (this % oneHour / oneMinute) != 0L
val Long.hasSeconds get() = (this % oneMinute / oneSecond) != 0L

private val durationRegex by lazy { "h+|m+|s+|S+".toRegex() }

fun Long.durationFormat(pattern: String): String =
    pattern.replace(durationRegex) { match ->
        when (match.value.take(2)) {
            "h" -> (this / oneHour % 24).toString()
            "hh" -> "%0${match.value.length}d".format(this / oneHour % 24)
            "m" -> (this / oneMinute % 60).toString()
            "mm" -> "%0${match.value.length}d".format(this / oneMinute % 60)
            "s" -> (this / oneSecond % 60).toString()
            "ss" -> "%0${match.value.length}d".format(this / oneSecond % 60)
            "S" -> (this % 1000L).toString()
            "SS" -> "%0${match.value.length}d".format(this % 1000)
            else -> match.value
        }
    }
