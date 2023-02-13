package com.rh.heji.data.converters

import androidx.room.TypeConverter
import com.squareup.moshi.FromJson

/**
 *@date: 2023/2/13
 *@author: 锅得铁
 *#
 */
object LogicConverters {
    const val YES = 1
    const val NO = 0

    @FromJson
    @JvmStatic
    @TypeConverter
    fun bool2Int(boolean: Boolean): Int {
        return if (boolean) {
            YES
        } else {
            NO
        }
    }

    @FromJson
    @JvmStatic
    @TypeConverter
    fun int2Bool(int: Int): Boolean {
        return int == YES
    }
}