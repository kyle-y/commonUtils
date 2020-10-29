package com.example.administrator.locallog

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

fun Long.isIn(small: Long, bigger: Long): Boolean {
    return this in small..bigger
}

fun Long.formatDuring(): String {
    var second = this / 1000
    val days = second / 86400
    second %= 86400
    val hours = second / 3600
    second %= 3600
    val minutes = second / 60
    second %= 60
    return when {
        days > 0 -> "${days}天${hours}小时${minutes}分${second}秒"
        hours > 0 -> "${hours}小时${minutes}分${second}秒"
        minutes > 0 -> "${minutes}分${second}秒"
        else -> "${second}秒"
    }
}


private val dateFormatThreadLocal: ThreadLocal<SimpleDateFormat> =
    object : ThreadLocal<SimpleDateFormat>() {
        @SuppressLint("SimpleDateFormat")
        override fun initialValue(): SimpleDateFormat = SimpleDateFormat()
    }

fun Date.toFormat(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val dateFormat = dateFormatThreadLocal.get() ?: return ""
    dateFormat.applyPattern(format)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(this)
}

@SuppressLint("SimpleDateFormat")
fun String.formatDateMil(format: String = "yyyy-MM-dd HH:mm:ss"): Long{
    val dateFormat = dateFormatThreadLocal.get() ?: SimpleDateFormat()
    dateFormat.applyPattern(format)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.parse(this).time
}