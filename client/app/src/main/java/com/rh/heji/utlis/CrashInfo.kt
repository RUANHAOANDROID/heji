package com.rh.heji.utlis

import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.rh.heji.data.db.ErrorLog

/**
 *Date: 2021/6/1
 *Author: 锅得铁
 *#
 */
open class CrashInfo() : CrashUtils.OnCrashListener {
    override fun onCrash(crashInfo: CrashUtils.CrashInfo) {
        crashInfo.addExtraHead(model, DeviceUtils.getModel())
        crashInfo.addExtraHead(sdkVersionName, DeviceUtils.getSDKVersionName())
        crashInfo.addExtraHead(sdkVersionCode, DeviceUtils.getSDKVersionCode().toString())
        crashInfo.addExtraHead(isTablet, DeviceUtils.isTablet().toString())
        crashInfo.addExtraHead(isEmulator, DeviceUtils.isEmulator().toString())
        crashInfo.addExtraHead(uniqueDeviceId, DeviceUtils.getUniqueDeviceId().toString())
        crashInfo.addExtraHead(networkType, NetworkUtils.getNetworkType().toString())
    }
    fun postError2Server(crashInfo: CrashUtils.CrashInfo){

    }
    fun save2DB(info: CrashUtils.CrashInfo){
        val errorLog =ErrorLog();
        errorLog.extra =info.toString()
    }
    companion object {

        const val sdkVersionName = "sdkVersionName"
        const val sdkVersionCode = "sdkVersionCode"
        const val isTablet = "isTablet"
        const val isEmulator = "isEmulator"
        const val uniqueDeviceId = "uniqueDeviceId"
        const val networkType = "networkType"
        const val model = "model"
    }
}