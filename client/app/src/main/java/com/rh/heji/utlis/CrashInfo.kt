package com.rh.heji.utlis

import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.rh.heji.BuildConfig
import com.rh.heji.currentUser
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.ErrorLog
import java.io.PrintWriter
import java.io.StringWriter

/**
 *Date: 2021/6/1
 *Author: 锅得铁
 *#
 */
open class CrashInfo : CrashUtils.OnCrashListener {
    override fun onCrash(crashInfo: CrashUtils.CrashInfo) {
        crashInfo.addExtraHead(isTablet, DeviceUtils.isTablet().toString())
        crashInfo.addExtraHead(isEmulator, DeviceUtils.isEmulator().toString())
        crashInfo.addExtraHead(uniqueDeviceId, DeviceUtils.getUniqueDeviceId().toString())
        crashInfo.addExtraHead(networkType, NetworkUtils.getNetworkType().toString())
        var errorLog = errorLogEntity(crashInfo)
        save2DB(errorLog)
    }

    private fun errorLogEntity(crashInfo: CrashUtils.CrashInfo): ErrorLog {
        val errorLog = ErrorLog()
        errorLog.appVersionCode = BuildConfig.VERSION_CODE.toString()
        errorLog.appVersionName = BuildConfig.VERSION_NAME
        errorLog.userid = currentUser.username
        errorLog.deviceModel = DeviceUtils.getModel()
        errorLog.isEmulator = DeviceUtils.isEmulator()
        errorLog.isTablet = DeviceUtils.isTablet()
        errorLog.networkType = NetworkUtils.getNetworkType().toString()
        errorLog.uniqueDeviceId = DeviceUtils.getUniqueDeviceId()
        errorLog.sdkVersionCode = DeviceUtils.getSDKVersionCode().toString()
        errorLog.sdkVersionName = DeviceUtils.getSDKVersionName()
        LogUtils.e(crashInfo.throwable)
        errorLog.crashContent = getExceptionToString(crashInfo.throwable)
        return errorLog
    }

    private fun getExceptionToString(e: Throwable): String {
        var stringWriter = StringWriter()
        e.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }

    private fun save2DB(errorLog: ErrorLog) {
        AppDatabase.getInstance().errorLogDao().install(errorLog)
    }

    companion object {
        const val isTablet = "isTablet"
        const val isEmulator = "isEmulator"
        const val uniqueDeviceId = "uniqueDeviceId"
        const val networkType = "networkType"
    }
}