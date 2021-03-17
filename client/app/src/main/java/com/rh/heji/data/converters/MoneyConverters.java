package com.rh.heji.data.converters;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #货币转换
 */
public class MoneyConverters {
    @TypeConverter
    public static BigDecimal fromLong(Long value) {
        BigDecimal bigDecimal = value == null ? null : new BigDecimal(value).divide(new BigDecimal(100));
        return bigDecimal;
    }

    @TypeConverter
    public static Long toLong(BigDecimal bigDecimal) {
        Long longValue = bigDecimal.multiply(new BigDecimal(100)).longValue();
        return longValue;
    }
}
