package com.rh.heji

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rh.heji.data.converters.DateConverters
import java.io.File
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