package com.rh.heji.data.converters

import androidx.room.Ignore
import androidx.room.TypeConverter
import java.math.BigDecimal

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #货币转换
 */
object MoneyConverters {
    @JvmStatic
    @TypeConverter
    fun fromLong(value: Long?): BigDecimal? {
        //.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_DOWN);//两位小数 整数补00
        return if (value == null) null else BigDecimal(value).divide(
            BigDecimal(100),
            2,
            BigDecimal.ROUND_DOWN
        )
    }

    @JvmStatic
    @TypeConverter
    fun toLong(bigDecimal: BigDecimal): Long {
        return bigDecimal.multiply(BigDecimal(100)).toLong()
    }

    @JvmStatic
    @Ignore
    fun ZERO_00(): BigDecimal {
        return BigDecimal.ZERO.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_DOWN)
    }
}