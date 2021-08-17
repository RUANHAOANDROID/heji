package com.rh.heji

import com.rh.heji.data.converters.DateConverters
import java.util.*

fun Date.string(): String {
    return DateConverters.date2Str(this)
}

fun String.date(): Date {
    return DateConverters.str2Date(this)
}