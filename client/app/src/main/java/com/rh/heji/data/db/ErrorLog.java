package com.rh.heji.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.rh.heji.data.db.mongo.ObjectId;

import org.jetbrains.annotations.Nullable;

/**
 * Date: 2021/3/2
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "error_log")
public class ErrorLog {

    @PrimaryKey()
    @NonNull
    String _id;

    @SerializedName("timeOfCrash")
    private long timeOfCrash;

    @SerializedName("uid")
    private String userid;

    private String deviceModel;

    private String sdkVersionName;

    private String sdkVersionCode;
    private String appVersionName;
    private String appVersionCode;
    private boolean isTablet;

    private boolean isEmulator;

    private String uniqueDeviceId;

    private String networkType;

    public String throwable;

    public ErrorLog() {
        this(new ObjectId());
    }

    @Ignore
    public ErrorLog(ObjectId objectId) {
        this._id = objectId.toString();
        this.timeOfCrash = System.currentTimeMillis();
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public long getTimeOfCrash() {
        return timeOfCrash;
    }

    public void setTimeOfCrash(long timeOfCrash) {
        this.timeOfCrash = timeOfCrash;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
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

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    public boolean isEmulator() {
        return isEmulator;
    }

    public void setEmulator(boolean emulator) {
        isEmulator = emulator;
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

    public String getCrashContent() {
        return throwable;
    }

    public void setCrashContent(String crashContent) {
        this.throwable = crashContent;
    }
}
