package com.mj.core.ktx

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.applyDateFormat(): String {
    val inputFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    val date = inputFormatter.parse(this) ?: return ""
    val outputFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
    outputFormatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")
    return outputFormatter.format(date)
}