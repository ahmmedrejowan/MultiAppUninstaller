package com.rejowan.multiappuninstaller.utils

import java.util.Locale

object DateFormatUtils {

    fun millisToDateTime(millis: Long): String {
        // to 08:00 AM, 01 Jan 2024
        val date = java.util.Date(millis)
        val format = java.text.SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault()) // 12-hour format
        return format.format(date)
    }
}