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
    public static final String DB_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String SHOW_PATTERN = "yyyy-MM-dd HH:mm";

    @TypeConverter
    public static Date str2Date(String value) {
        Date date = value == null ? null : TimeUtils.string2Date(value, DB_PATTERN);
        return date;
    }

    @TypeConverter
    public static String date2Str(Date date) {
        String time = date == null ? null : TimeUtils.date2String(date,DB_PATTERN);
        return time;
    }
}
