package com.rh.heji.data.converters;

import androidx.room.TypeConverter;

import com.blankj.utilcode.util.TimeUtils;

import java.util.Date;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #时间转换 -注意查的时候月份-1 ，存的时候不处理
 */
public class DateConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        Date date = value == null ? null : TimeUtils.millis2Date(value);
        return date;
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        Long time = date == null ? null : TimeUtils.date2Millis(date);
        return time;
    }
}
