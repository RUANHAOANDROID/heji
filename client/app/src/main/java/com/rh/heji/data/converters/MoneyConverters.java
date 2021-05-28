package com.rh.heji.data.converters;

import androidx.room.Ignore;
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
        BigDecimal bigDecimal = value == null ? null : new BigDecimal(value).divide(new BigDecimal(100))
                .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_DOWN);//两位小数 整数补00
        return bigDecimal;
    }

    @TypeConverter
    public static Long toLong(BigDecimal bigDecimal) {
        Long longValue = bigDecimal.multiply(new BigDecimal(100)).longValue();
        return longValue;
    }

    @Ignore
    public static BigDecimal ZERO_00() {
        return BigDecimal.ZERO.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_DOWN);
    }
}
