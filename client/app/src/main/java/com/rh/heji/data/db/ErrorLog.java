package com.rh.heji.data.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.rh.heji.data.db.mongo.ObjectId;

/**
 * Date: 2021/3/2
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "error_log")
public class ErrorLog {
    String objectId;

    @PrimaryKey
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

    @SerializedName("model")
    private String model;

    @SerializedName("sdkVersionName")
    private String sdkVersionName;

    @SerializedName("sdkVersionCode")
    private String sdkVersionCode;

    @SerializedName("isTablet")
    private String isTablet;

    @SerializedName("isEmulator")
    private String isEmulator;

    @SerializedName("uniqueDeviceId")
    private String uniqueDeviceId;

    @SerializedName("networkType")
    private String networkType;

    public ErrorLog() {
    }

    @Ignore
    public ErrorLog(ObjectId objectId) {
        this.objectId = objectId.toString();
        this.time = System.currentTimeMillis();
    }

    public String getObjectId() {
        return objectId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSdkVersionName() {
        return sdkVersionName;
    }

    public void setSdkVersionName(String sdkVersionName) {
        this.sdkVersionName = sdkVersionName;
    }

    public String getSdkVersionCode() {
        return sdkVersionCode;
    }

    public void setSdkVersionCode(String sdkVersionCode) {
        this.sdkVersionCode = sdkVersionCode;
    }

    public String getIsTablet() {
        return isTablet;
    }

    public void setIsTablet(String isTablet) {
        this.isTablet = isTablet;
    }

    public String getIsEmulator() {
        return isEmulator;
    }

    public void setIsEmulator(String isEmulator) {
        this.isEmulator = isEmulator;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}
