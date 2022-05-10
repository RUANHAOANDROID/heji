package com.rh.heji

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.rh.heji.data.converters.DateConverters
import java.util.*

/**
 * Kotlin 顶级扩展函数
 *@date 2022/3/1
 *@author 锅得铁
 *@constructor default constructor
 */
/**
 * 扩展 Context 以 dataStore
 * @receiver Context
 */
val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "settings")

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