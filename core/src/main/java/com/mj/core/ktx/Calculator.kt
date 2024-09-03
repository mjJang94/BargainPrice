package com.mj.core.ktx

import java.util.Locale

fun calculatePercentageDifferenceOrNull(currentPrice: String, previousPrice: String): String? {
    val current = currentPrice.toDoubleSafety()
    val prev = previousPrice.toDoubleSafety()

    if (prev == 0.0) return null

    val difference = prev - current
    val percentageDifference = (difference / current) * 100 * -1

    return when (kotlin.math.abs(percentageDifference) >= 0.1) {
        true -> String.format(Locale.getDefault(), "%.2f%%", percentageDifference)
        else -> null
    }
}

private fun String.toDoubleSafety(): Double = runCatching {
    this.toDouble()
}.getOrNull() ?: 0.0

fun String.toLongSafety(): Long = runCatching {
    this.toLong()
}.getOrNull() ?: 0L