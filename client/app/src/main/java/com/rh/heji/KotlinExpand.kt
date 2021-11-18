package com.rh.heji

import com.rh.heji.data.converters.DateConverters
import java.util.*

fun Date.string(): String {
    return DateConverters.date2Str(this)
}

fun String.date(): Date {
    return DateConverters.str2Date(this)
}

fun Date.calendar(): Calendar {
    val instance = Calendar.getInstance()
    instance.time = this
    return instance
}

fun String.getObjectTime(): Date {
    val time = "${Integer.parseInt(this.substring(0, 8), 16)}000".toLong()
    return Date(time)
}