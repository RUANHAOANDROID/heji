package com.rh.heji.utlis;

import android.util.Log;

import com.rh.heji.BuildConfig;


/**
 * Date: 2019-12-18
 * Author: 锅得铁
 * #
 */
public class Logger {
    static boolean isDebug = BuildConfig.DEBUG;
    static String TAG = "Logger";

    public static void d(String text) {
        if (!isDebug) return;
        Log.d(TAG, info(text));
    }

    public static void i(String text) {
        if (!isDebug) return;
        Log.i(TAG, info(text));
    }

    public static void e(String text) {
        if (!isDebug) return;
        Log.e(TAG, info(text));
    }

    public static void v(String text) {
        if (!isDebug) return;
        Log.v(TAG, info(text));
    }

    public static void w(String text) {
        if (!isDebug) return;
        Log.w(TAG, info(text));
    }

    private static String info(String text) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        builder
                .append("(")
                .append(trace[4].getFileName())
                .append(":")
                .append(trace[4].getLineNumber())
                .append(")")
                .append("\n")
                //.append("12-18 15:59:49.144 4740-4740/com.example.jetack ")
                .append("\t \t \t \t \t \t \t \t \t \t \t")
                .append(text)

        ;
        return builder.toString();
    }
}
