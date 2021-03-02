package com.rh.heji.data.db;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 2021/3/2
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "error_log")
public class ErrorLog {
    String objectId;

    @SerializedName("code")
    private int code;

    @SerializedName("extra")
    private String extra;

    @SerializedName("message")
    private String message;

    @SerializedName("time")
    private long time;

    @SerializedName("uid")
    private String userid;



}
