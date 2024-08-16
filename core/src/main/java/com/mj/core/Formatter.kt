@file:Suppress("unused")

package com.mj.core

import java.text.DecimalFormat
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

private val defaultFormatter =
    DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .toFormatter()

val Long.clock: Clock get() = EpochMilliClock(this)

val Long.localDateTime: LocalDateTime get() =
    clock.run { LocalDateTime.ofInstant(instant(), zone) }

fun Long.format(): String =
    defaultFormatter.format(localDateTime)

fun Long.format(pattern: String, locale: Locale = Locale.getDefault()): String =
    DateTimeFormatter.ofPattern(pattern, locale).format(localDateTime)

fun formatNow(): String =
    defaultFormatter.format(LocalDateTime.now())

fun formatNow(pattern: String, locale: Locale = Locale.getDefault()): String =
    DateTimeFormatter.ofPattern(pattern, locale).format(LocalDateTime.now())

fun String.toDateTimeFormatter(locale: Locale = Locale.getDefault()): DateTimeFormatter =
    DateTimeFormatter.ofPattern(this, locale)

fun String.toPriceFormat() : String? =
    runCatching { DecimalFormat("#,###").format(this.toInt()) }.getOrNull()
